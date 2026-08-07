package com.example.data.repository

import com.example.data.local.*
import com.example.data.remote.AiMealSwapItem
import com.example.data.remote.GeminiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HerbalanceRepository(private val dao: HerbalanceDao) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val weightLogs: Flow<List<WeightLogEntity>> = dao.getAllWeightLogs()
    val latestWeight: Flow<WeightLogEntity?> = dao.getLatestWeightLog()
    val allRecipes: Flow<List<RecipeEntity>> = dao.getAllRecipes()
    val notifications: Flow<List<AppNotificationEntity>> = dao.getNotifications()

    fun getFoodLogsForDate(date: String): Flow<List<FoodLogEntity>> = dao.getFoodLogsForDate(date)
    fun getWaterLogsForDate(date: String): Flow<List<WaterLogEntity>> = dao.getWaterLogsForDate(date)
    fun getTotalWaterForDate(date: String): Flow<Int?> = dao.getTotalWaterForDate(date)
    fun getMealPlanForMonth(monthYear: String): Flow<List<MealPlanEntity>> = dao.getMealPlanForMonth(monthYear)
    fun getShoppingListForMonth(monthYear: String): Flow<List<ShoppingItemEntity>> = dao.getShoppingListForMonth(monthYear)
    fun getCheckInForDate(date: String): Flow<DailyCheckInEntity?> = dao.getCheckInForDate(date)

    suspend fun initDefaultDataIfNeeded() = withContext(Dispatchers.IO) {
        val profile = dao.getUserProfileOnce()
        if (profile == null) {
            val defaultProfile = UserProfileEntity(
                id = 1,
                name = "Beautiful",
                email = "user@herbalance.app",
                age = 35,
                heightCm = 168f,
                weightKg = 68f,
                unitSystem = "Metric",
                goals = "Eat healthier, Improve energy, Build better eating habits",
                activityLevel = "Moderately active",
                cuisinePreferences = "Nigerian, West African, Mediterranean, Asian",
                dietaryRestrictions = "No restrictions",
                allergies = "None",
                waterTargetMl = 2000,
                calorieTarget = 1850,
                isGiftMode = true,
                recipientName = "Beautiful",
                birthdayMsg = "This little space was created with you in mind—not just for today, but for all the healthier, happier years ahead.",
                onboardingCompleted = true,
                isLoggedIn = true
            )
            dao.insertOrUpdateProfile(defaultProfile)
            seedSampleRecipesAndData(defaultProfile)
        }
    }

    private suspend fun seedSampleRecipesAndData(profile: UserProfileEntity) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

        // Initial Weight Logs
        dao.insertWeightLog(WeightLogEntity(date = "2026-08-01", weightKg = 70.2f, note = "Start of month"))
        dao.insertWeightLog(WeightLogEntity(date = "2026-08-04", weightKg = 69.4f, note = "Feeling good"))
        dao.insertWeightLog(WeightLogEntity(date = today, weightKg = 68.0f, note = "Current progress"))

        // Initial Water Logs
        dao.insertWaterLog(WaterLogEntity(date = today, amountMl = 250))
        dao.insertWaterLog(WaterLogEntity(date = today, amountMl = 500))
        dao.insertWaterLog(WaterLogEntity(date = today, amountMl = 500))

        // Initial Food Logs
        dao.insertFoodLog(FoodLogEntity(date = today, mealType = "Breakfast", name = "Warm Berry Cinnamon Oatmeal Bowl", portionSize = 1f, portionUnit = "bowl", calories = 380, protein = 16f, carbs = 54f, fat = 8f, fiber = 7f))
        dao.insertFoodLog(FoodLogEntity(date = today, mealType = "Lunch", name = "Mediterranean Quinoa & Chickpea Bowl", portionSize = 1f, portionUnit = "bowl", calories = 520, protein = 24f, carbs = 68f, fat = 14f, fiber = 11f))

        // Sample Recipes
        val recipes = listOf(
            RecipeEntity(
                title = "Akara (Bean Cakes) & Fresh Pap",
                description = "Traditional golden fried peeled bean fritters packed with plant protein.",
                prepTimeMinutes = 20, cookTimeMinutes = 15, servings = 2,
                calories = 380, protein = 18f, carbs = 42f, fat = 12f, fiber = 8f,
                ingredientsJson = "[\"2 cups peeled brown beans\", \"1 medium red onion\", \"1 habanero pepper\", \"1 tsp salt\", \"Olive oil for shallow frying\"]",
                instructionsJson = "[\"Blend peeled beans with onion, pepper, and minimal water until smooth paste.\", \"Whisk batter vigorously to introduce air.\", \"Heat oil in pan and scoop batter into round cakes.\", \"Fry until golden brown on both sides. Serve hot.\"]",
                category = "Cultural"
            ),
            RecipeEntity(
                title = "Lemon Herb Salmon & Sweet Potato Mash",
                description = "Succulent wild salmon with garlic-baked sweet potato and asparagus.",
                prepTimeMinutes = 15, cookTimeMinutes = 20, servings = 2,
                calories = 560, protein = 42f, carbs = 38f, fat = 22f, fiber = 7f,
                ingredientsJson = "[\"2 salmon fillets\", \"1 tbsp olive oil\", \"1 lemon juice & zest\", \"2 garlic cloves minced\", \"2 sweet potatoes\", \"1 bunch asparagus\"]",
                instructionsJson = "[\"Preheat oven to 200°C (400°F).\", \"Season salmon with olive oil, lemon, garlic, salt, and black pepper.\", \"Boil sweet potatoes and mash with a touch of olive oil.\", \"Roast salmon and asparagus for 15 minutes. Assemble bowl.\"]",
                category = "Dinner"
            ),
            RecipeEntity(
                title = "Steamed Moi Moi & Garden Salad",
                description = "Nutritious steamed bean pudding rich in folate, iron, and protein.",
                prepTimeMinutes = 25, cookTimeMinutes = 40, servings = 4,
                calories = 320, protein = 22f, carbs = 36f, fat = 6f, fiber = 10f,
                ingredientsJson = "[\"3 cups peeled honey beans\", \"2 red bell peppers\", \"1 onion\", \"2 hard-boiled eggs sliced\", \"2 tbsp vegetable oil\", \"Seasoning cube & salt\"]",
                instructionsJson = "[\"Blend beans, peppers, onion, and warm water into silky batter.\", \"Season with oil, salt, and spices.\", \"Pour into ramekins or banana leaves, insert egg slice.\", \"Steam over boiling water for 35-40 mins until set.\"]",
                category = "Lunch"
            ),
            RecipeEntity(
                title = "Avocado & Poached Egg Toast",
                description = "Creamy ripe avocado on toasted sourdough topped with free-range poached egg.",
                prepTimeMinutes = 10, cookTimeMinutes = 5, servings = 1,
                calories = 390, protein = 16f, carbs = 32f, fat = 22f, fiber = 8f,
                ingredientsJson = "[\"2 slices sourdough bread\", \"1 mature avocado\", \"2 fresh eggs\", \"Chili flakes\", \"1 tsp lemon juice\"]",
                instructionsJson = "[\"Toast sourdough slices.\", \"Mash avocado with lemon juice, salt, and pepper.\", \"Poach eggs in simmering water for 3 minutes.\", \"Spread avocado on toast and top with poached egg and chili flakes.\"]",
                category = "Breakfast"
            )
        )
        dao.insertRecipes(recipes)

        // Seed Initial Meal Plan
        val initialPlan = GeminiService.generateMonthlyMealPlan(profile, currentMonth)
        dao.insertMealPlans(initialPlan)

        // Seed Shopping List
        val shoppingItems = listOf(
            ShoppingItemEntity(monthYear = currentMonth, category = "Vegetables", itemName = "Spinach & Kale", quantity = "2 bags"),
            ShoppingItemEntity(monthYear = currentMonth, category = "Vegetables", itemName = "Sweet Potatoes", quantity = "1 bag (2kg)"),
            ShoppingItemEntity(monthYear = currentMonth, category = "Vegetables", itemName = "Avocados", quantity = "4 ripe"),
            ShoppingItemEntity(monthYear = currentMonth, category = "Protein", itemName = "Fresh Salmon Fillets", quantity = "4 pieces"),
            ShoppingItemEntity(monthYear = currentMonth, category = "Protein", itemName = "Honey / Brown Beans", quantity = "1 bag (1kg)"),
            ShoppingItemEntity(monthYear = currentMonth, category = "Grains", itemName = "Rolled Oats", quantity = "1 tub"),
            ShoppingItemEntity(monthYear = currentMonth, category = "Grains", itemName = "Quinoa & Brown Rice", quantity = "1 pack"),
            ShoppingItemEntity(monthYear = currentMonth, category = "Dairy/Alternatives", itemName = "Greek Yogurt", quantity = "2 tubs"),
            ShoppingItemEntity(monthYear = currentMonth, category = "Pantry", itemName = "Extra Virgin Olive Oil", quantity = "1 bottle")
        )
        dao.insertShoppingItems(shoppingItems)

        // Seed Welcome Notification
        dao.insertNotification(AppNotificationEntity(
            title = "Welcome to HERBALANCE",
            message = "Your personalized nutrition workspace is ready. Let's make today a nourishing one! 🌿",
            category = "Monthly"
        ))
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun logFood(log: FoodLogEntity) = withContext(Dispatchers.IO) {
        dao.insertFoodLog(log)
    }

    suspend fun deleteFood(log: FoodLogEntity) = withContext(Dispatchers.IO) {
        dao.deleteFoodLog(log)
    }

    suspend fun addWater(amountMl: Int, date: String) = withContext(Dispatchers.IO) {
        dao.insertWaterLog(WaterLogEntity(date = date, amountMl = amountMl))
    }

    suspend fun logWeight(weightKg: Float, note: String, date: String) = withContext(Dispatchers.IO) {
        dao.insertWeightLog(WeightLogEntity(date = date, weightKg = weightKg, note = note))
    }

    suspend fun generateFreshMonthlyPlan(monthYear: String) = withContext(Dispatchers.IO) {
        val profile = dao.getUserProfileOnce() ?: return@withContext
        val newPlan = GeminiService.generateMonthlyMealPlan(profile, monthYear)
        dao.deleteMealPlanForMonth(monthYear)
        dao.insertMealPlans(newPlan)
    }

    suspend fun getMealSwaps(originalMealName: String): List<AiMealSwapItem> = withContext(Dispatchers.IO) {
        val profile = dao.getUserProfileOnce() ?: UserProfileEntity()
        GeminiService.generateMealSwaps(originalMealName, profile)
    }

    suspend fun updateMealPlan(plan: MealPlanEntity) = withContext(Dispatchers.IO) {
        dao.updateMealPlan(plan)
    }

    suspend fun toggleShoppingItem(item: ShoppingItemEntity) = withContext(Dispatchers.IO) {
        dao.updateShoppingItem(item.copy(isChecked = !item.isChecked))
    }

    suspend fun addShoppingItem(item: ShoppingItemEntity) = withContext(Dispatchers.IO) {
        dao.insertShoppingItem(item)
    }

    suspend fun deleteShoppingItem(item: ShoppingItemEntity) = withContext(Dispatchers.IO) {
        dao.deleteShoppingItem(item)
    }

    suspend fun logCheckIn(checkIn: DailyCheckInEntity) = withContext(Dispatchers.IO) {
        dao.insertCheckIn(checkIn)
    }
}
