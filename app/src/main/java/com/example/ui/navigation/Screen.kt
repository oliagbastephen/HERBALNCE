package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Landing : Screen("landing", "Landing")
    object Onboarding : Screen("onboarding", "Onboarding")
    object Dashboard : Screen("dashboard", "Home")
    object Meals : Screen("meals", "Meal Plan")
    object FoodLog : Screen("food_log", "Food Log")
    object Water : Screen("water", "Water")
    object Weight : Screen("weight", "Weight")
    object Progress : Screen("progress", "Progress")
    object Recipes : Screen("recipes", "Recipes")
    object RecipeDetail : Screen("recipe_detail/{recipeId}", "Recipe Detail") {
        fun createRoute(recipeId: Long) = "recipe_detail/$recipeId"
    }
    object ShoppingList : Screen("shopping_list", "Shopping List")
    object Learn : Screen("learn", "Learn")
    object DailyCheckIn : Screen("daily_checkin", "Daily Check-In")
    object Profile : Screen("profile", "Profile")
    object Settings : Screen("settings", "Settings")
    object Admin : Screen("admin", "Admin")
    object VoiceConversation : Screen("voice_conversation", "Voice Assistant")
}
