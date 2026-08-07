package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.local.MealPlanEntity
import com.example.ui.components.*
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.HerbalanceTheme
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HerbalanceTheme {
                HerbalanceApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HerbalanceApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val foodLogs by viewModel.foodLogs.collectAsStateWithLifecycle()
    val totalWaterMl by viewModel.totalWaterMl.collectAsStateWithLifecycle()
    val weightLogs by viewModel.weightLogs.collectAsStateWithLifecycle()
    val mealPlan by viewModel.mealPlan.collectAsStateWithLifecycle()
    val recipes by viewModel.recipes.collectAsStateWithLifecycle()
    val shoppingList by viewModel.shoppingList.collectAsStateWithLifecycle()
    val dailyCheckIn by viewModel.dailyCheckIn.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val currentMonth by viewModel.currentMonthYear.collectAsStateWithLifecycle()
    val isGeneratingAiPlan by viewModel.isGeneratingAiPlan.collectAsStateWithLifecycle()
    val aiTip by viewModel.aiTip.collectAsStateWithLifecycle()
    val swapAlternatives by viewModel.swapAlternatives.collectAsStateWithLifecycle()
    val isSwapLoading by viewModel.isSwapLoading.collectAsStateWithLifecycle()

    // Dialog States
    var showWaterDialog by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var mealForSwap by remember { mutableStateOf<MealPlanEntity?>(null) }
    var showBirthdayDialog by remember { mutableStateOf(true) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

    val showBars = currentRoute != Screen.Landing.route && currentRoute != Screen.Onboarding.route

    val navigateToScreen: (Screen) -> Unit = { screen ->
        if (currentRoute != screen.route) {
            if (screen == Screen.Dashboard) {
                val popped = navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                if (!popped) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            } else {
                navController.navigate(screen.route) {
                    popUpTo(Screen.Dashboard.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBars,
        drawerContent = {
            HerbalanceDrawerContent(
                currentRoute = currentRoute,
                onNavigate = navigateToScreen,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (showBars) {
                    HerbalanceTopBar(
                        currentScreen = Screen.Dashboard,
                        onNavigate = navigateToScreen,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
            },
            bottomBar = {
                if (showBars) {
                    HerbalanceBottomNav(
                        currentRoute = currentRoute,
                        onNavigate = navigateToScreen
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = if (profile?.onboardingCompleted == true) Screen.Dashboard.route else Screen.Landing.route
                ) {
                    composable(Screen.Landing.route) {
                        LandingScreen(
                            onStartOnboarding = { navController.navigate(Screen.Onboarding.route) },
                            onDirectDashboard = { navController.navigate(Screen.Dashboard.route) }
                        )
                    }

                    composable(Screen.Onboarding.route) {
                        OnboardingScreen(
                            currentProfile = profile,
                            onCompleteOnboarding = { updatedProfile ->
                                viewModel.updateProfile(updatedProfile)
                                viewModel.generateFreshMonthlyPlan()
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Landing.route) { inclusive = true }
                                }
                            },
                            onBackToLanding = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Dashboard.route) {
                        DashboardScreen(
                            profile = profile,
                            foodLogs = foodLogs,
                            totalWaterMl = totalWaterMl,
                            weightLogs = weightLogs,
                            mealPlan = mealPlan,
                            dailyTip = aiTip,
                            onNavigate = navigateToScreen,
                            onOpenLogWater = { showWaterDialog = true },
                            onOpenLogWeight = { showWeightDialog = true },
                            onOpenMealSwap = { meal ->
                                mealForSwap = meal
                                viewModel.fetchMealSwaps(meal.mealName)
                            },
                            onQuickLogMeal = { meal ->
                                viewModel.logFood(
                                    mealType = meal.mealType,
                                    name = meal.mealName,
                                    portionSize = 1f,
                                    portionUnit = "serving",
                                    calories = meal.calories,
                                    protein = meal.protein.toFloat(),
                                    carbs = 40f,
                                    fat = 12f,
                                    fiber = meal.fiber.toFloat()
                                )
                            }
                        )
                    }

                    composable(Screen.Meals.route) {
                        MealPlanScreen(
                            currentMonth = currentMonth,
                            mealPlans = mealPlan,
                            isGenerating = isGeneratingAiPlan,
                            onGenerateFreshPlan = { viewModel.generateFreshMonthlyPlan() },
                            onOpenSwap = { meal ->
                                mealForSwap = meal
                                viewModel.fetchMealSwaps(meal.mealName)
                            },
                            onLogMeal = { meal ->
                                viewModel.logFood(
                                    mealType = meal.mealType,
                                    name = meal.mealName,
                                    portionSize = 1f,
                                    portionUnit = "serving",
                                    calories = meal.calories,
                                    protein = meal.protein.toFloat(),
                                    carbs = 40f,
                                    fat = 12f,
                                    fiber = meal.fiber.toFloat()
                                )
                            }
                        )
                    }

                    composable(Screen.FoodLog.route) {
                        FoodLogScreen(
                            selectedDate = selectedDate,
                            foodLogs = foodLogs,
                            profile = profile,
                            onDateChange = { date -> viewModel.setSelectedDate(date) },
                            onLogFood = { mType, name, pSize, pUnit, cal, prot, carbs, fat, fib ->
                                viewModel.logFood(mType, name, pSize, pUnit, cal, prot, carbs, fat, fib)
                            },
                            onDeleteFood = { log -> viewModel.deleteFood(log) }
                        )
                    }

                    composable(Screen.Water.route) {
                        WaterScreen(
                            totalWaterMl = totalWaterMl,
                            profile = profile,
                            onAddWater = { amount -> viewModel.addWater(amount) }
                        )
                    }

                    composable(Screen.Weight.route) {
                        WeightScreen(
                            weightLogs = weightLogs,
                            onOpenLogWeight = { showWeightDialog = true }
                        )
                    }

                    composable(Screen.Progress.route) {
                        ProgressScreen()
                    }

                    composable(Screen.Recipes.route) {
                        RecipesScreen(
                            recipes = recipes,
                            onSelectRecipe = { id -> navController.navigate(Screen.RecipeDetail.createRoute(id)) }
                        )
                    }

                    composable(Screen.RecipeDetail.route) { backStackEntry ->
                        val recipeId = backStackEntry.arguments?.getString("recipeId")?.toLongOrNull() ?: 1L
                        val recipe = recipes.find { it.id == recipeId }
                        RecipeDetailScreen(
                            recipe = recipe,
                            onBack = { navController.popBackStack() },
                            onAddIngredientsToShoppingList = { ingredients ->
                                ingredients.forEach { ing ->
                                    viewModel.addShoppingItem("Recipe Ingredients", ing, "1 pack")
                                }
                                navController.navigate(Screen.ShoppingList.route)
                            }
                        )
                    }

                    composable(Screen.ShoppingList.route) {
                        ShoppingListScreen(
                            currentMonth = currentMonth,
                            shoppingItems = shoppingList,
                            onToggleItem = { item -> viewModel.toggleShoppingItem(item) },
                            onAddItem = { cat, name, qty -> viewModel.addShoppingItem(cat, name, qty) },
                            onDeleteItem = { item -> viewModel.deleteShoppingItem(item) }
                        )
                    }

                    composable(Screen.Learn.route) {
                        LearnScreen()
                    }

                    composable(Screen.DailyCheckIn.route) {
                        DailyCheckInScreen(
                            currentCheckIn = dailyCheckIn,
                            onSaveCheckIn = { mood, energy, note ->
                                viewModel.logCheckIn(mood, energy, note)
                                navigateToScreen(Screen.Dashboard)
                            }
                        )
                    }

                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            profile = profile,
                            onNavigate = navigateToScreen,
                            onEditProfile = { navController.navigate(Screen.Onboarding.route) }
                        )
                    }

                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            profile = profile,
                            onNavigate = navigateToScreen,
                            onUpdateProfile = { updated -> viewModel.updateProfile(updated) }
                        )
                    }

                    composable(Screen.Admin.route) {
                        AdminScreen(
                            profile = profile,
                            onUpdateBirthdayMsg = { msg ->
                                profile?.let { p -> viewModel.updateProfile(p.copy(birthdayMsg = msg)) }
                            }
                        )
                    }

                    composable(Screen.VoiceConversation.route) {
                        VoiceConversationScreen(
                            userProfile = profile,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    // Interactive Dialog Overlays
    if (showWaterDialog) {
        LogWaterDialog(
            onDismiss = { showWaterDialog = false },
            onAddWater = { amount -> viewModel.addWater(amount) }
        )
    }

    if (showWeightDialog) {
        LogWeightDialog(
            currentWeight = weightLogs.lastOrNull()?.weightKg ?: profile?.weightKg ?: 68f,
            onDismiss = { showWeightDialog = false },
            onSaveWeight = { w, note -> viewModel.logWeight(w, note) }
        )
    }

    mealForSwap?.let { meal ->
        MealSwapDialog(
            originalMeal = meal,
            alternatives = swapAlternatives,
            isLoading = isSwapLoading,
            onDismiss = { mealForSwap = null },
            onSelectSwap = { selectedSwap ->
                viewModel.swapMeal(meal, selectedSwap)
                mealForSwap = null
            }
        )
    }

    if (showBirthdayDialog && profile?.isGiftMode == true) {
        BirthdayWelcomeDialog(
            recipientName = profile?.recipientName ?: "Beautiful",
            birthdayMsg = profile?.birthdayMsg ?: "This little space was created with you in mind—not just for today, but for all the healthier, happier years ahead.",
            onDismiss = { showBirthdayDialog = false }
        )
    }
}
