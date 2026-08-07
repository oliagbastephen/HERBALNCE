package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.remote.AiMealSwapItem
import com.example.data.remote.GeminiService
import com.example.data.repository.HerbalanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = HerbalanceDatabase.getDatabase(application)
    private val repository = HerbalanceRepository(db.herbalanceDao())

    private val _selectedDate = MutableStateFlow(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    )
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _currentMonthYear = MutableStateFlow(
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    )
    val currentMonthYear: StateFlow<String> = _currentMonthYear.asStateFlow()

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val foodLogs: StateFlow<List<FoodLogEntity>> = _selectedDate.flatMapLatest { date ->
        repository.getFoodLogsForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalWaterMl: StateFlow<Int> = _selectedDate.flatMapLatest { date ->
        repository.getTotalWaterForDate(date).map { it ?: 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weightLogs: StateFlow<List<WeightLogEntity>> = repository.weightLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mealPlan: StateFlow<List<MealPlanEntity>> = _currentMonthYear.flatMapLatest { month ->
        repository.getMealPlanForMonth(month)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recipes: StateFlow<List<RecipeEntity>> = repository.allRecipes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shoppingList: StateFlow<List<ShoppingItemEntity>> = _currentMonthYear.flatMapLatest { month ->
        repository.getShoppingListForMonth(month)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyCheckIn: StateFlow<DailyCheckInEntity?> = _selectedDate.flatMapLatest { date ->
        repository.getCheckInForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notifications: StateFlow<List<AppNotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isGeneratingAiPlan = MutableStateFlow(false)
    val isGeneratingAiPlan: StateFlow<Boolean> = _isGeneratingAiPlan.asStateFlow()

    private val _aiTip = MutableStateFlow("Today's Tip: Pairing complex carbs with lean protein and fiber supports steady blood sugar and vibrant daily energy.")
    val aiTip: StateFlow<String> = _aiTip.asStateFlow()

    private val _swapAlternatives = MutableStateFlow<List<AiMealSwapItem>>(emptyList())
    val swapAlternatives: StateFlow<List<AiMealSwapItem>> = _swapAlternatives.asStateFlow()

    private val _isSwapLoading = MutableStateFlow(false)
    val isSwapLoading: StateFlow<Boolean> = _isSwapLoading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initDefaultDataIfNeeded()
            refreshDailyTip()
        }
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun refreshDailyTip() {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfileEntity()
            val tip = GeminiService.generateDailyTip(profile, totalWaterMl.value)
            _aiTip.value = tip
        }
    }

    fun logFood(
        mealType: String,
        name: String,
        portionSize: Float,
        portionUnit: String,
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float,
        fiber: Float
    ) {
        viewModelScope.launch {
            val log = FoodLogEntity(
                date = _selectedDate.value,
                mealType = mealType,
                name = name,
                portionSize = portionSize,
                portionUnit = portionUnit,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat,
                fiber = fiber
            )
            repository.logFood(log)
        }
    }

    fun deleteFood(log: FoodLogEntity) {
        viewModelScope.launch {
            repository.deleteFood(log)
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.addWater(amountMl, _selectedDate.value)
            refreshDailyTip()
        }
    }

    fun logWeight(weightKg: Float, note: String) {
        viewModelScope.launch {
            repository.logWeight(weightKg, note, _selectedDate.value)
            userProfile.value?.let { profile ->
                repository.saveUserProfile(profile.copy(weightKg = weightKg))
            }
        }
    }

    fun updateProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun generateFreshMonthlyPlan() {
        viewModelScope.launch {
            _isGeneratingAiPlan.value = true
            repository.generateFreshMonthlyPlan(_currentMonthYear.value)
            _isGeneratingAiPlan.value = false
        }
    }

    fun fetchMealSwaps(mealName: String) {
        viewModelScope.launch {
            _isSwapLoading.value = true
            _swapAlternatives.value = repository.getMealSwaps(mealName)
            _isSwapLoading.value = false
        }
    }

    fun swapMeal(originalMeal: MealPlanEntity, newSwap: AiMealSwapItem) {
        viewModelScope.launch {
            val updated = originalMeal.copy(
                mealName = newSwap.mealName,
                description = newSwap.description,
                calories = newSwap.calories,
                protein = newSwap.protein,
                fiber = newSwap.fiber,
                prepTimeMinutes = newSwap.prepTimeMinutes,
                isSwapped = true,
                originalMealName = originalMeal.mealName
            )
            repository.updateMealPlan(updated)
            _swapAlternatives.value = emptyList()
        }
    }

    fun toggleShoppingItem(item: ShoppingItemEntity) {
        viewModelScope.launch {
            repository.toggleShoppingItem(item)
        }
    }

    fun addShoppingItem(category: String, itemName: String, quantity: String) {
        viewModelScope.launch {
            val newItem = ShoppingItemEntity(
                monthYear = _currentMonthYear.value,
                category = category,
                itemName = itemName,
                quantity = quantity
            )
            repository.addShoppingItem(newItem)
        }
    }

    fun deleteShoppingItem(item: ShoppingItemEntity) {
        viewModelScope.launch {
            repository.deleteShoppingItem(item)
        }
    }

    fun logCheckIn(mood: String, energyLevel: String, note: String) {
        viewModelScope.launch {
            val checkIn = DailyCheckInEntity(
                date = _selectedDate.value,
                mood = mood,
                energyLevel = energyLevel,
                note = note
            )
            repository.logCheckIn(checkIn)
        }
    }
}
