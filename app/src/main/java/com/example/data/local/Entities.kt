package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Beautiful",
    val email: String = "user@herbalance.app",
    val phone: String = "",
    val age: Int = 35,
    val heightCm: Float = 168f,
    val weightKg: Float = 68f,
    val unitSystem: String = "Metric", // Metric or Imperial
    val goals: String = "Eat healthier, Improve energy, Build better eating habits", // comma separated
    val activityLevel: String = "Moderately active",
    val cuisinePreferences: String = "Nigerian, West African, Mediterranean, Asian", // comma separated
    val dietaryRestrictions: String = "No restrictions",
    val allergies: String = "None",
    val mealsPerDay: Int = 3,
    val cookingTime: String = "30-45 mins",
    val budget: String = "Balanced",
    val waterTargetMl: Int = 2000,
    val calorieTarget: Int = 1850,
    val proteinTargetGrams: Int = 85,
    val carbsTargetGrams: Int = 210,
    val fatTargetGrams: Int = 60,
    val fiberTargetGrams: Int = 28,
    val isGiftMode: Boolean = true,
    val recipientName: String = "Beautiful",
    val birthdayMsg: String = "This little space was created with you in mind—not just for today, but for all the healthier, happier years ahead.",
    val onboardingCompleted: Boolean = false,
    val isLoggedIn: Boolean = true,
    val authType: String = "Google", // Google, Email, Phone
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "food_logs")
data class FoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val name: String,
    val portionSize: Float,
    val portionUnit: String, // grams, cups, pieces, slices, bowls, servings, etc.
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fiber: Float,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "weight_logs")
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val weightKg: Float,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "meal_plans")
data class MealPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthYear: String, // e.g. "August 2026"
    val weekNumber: Int, // 1, 2, 3, 4
    val dayOfWeek: String, // Monday, Tuesday...
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val mealName: String,
    val recipeId: Long = 0,
    val calories: Int,
    val protein: Int,
    val fiber: Int,
    val prepTimeMinutes: Int,
    val isSwapped: Boolean = false,
    val originalMealName: String = "",
    val description: String = ""
)

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fiber: Float,
    val ingredientsJson: String, // List<String> JSON
    val instructionsJson: String, // List<String> JSON
    val category: String, // Breakfast, Lunch, Dinner, Snack, Cultural
    val imageResName: String = "img_hero_banner_1786142233110",
    val isFavorite: Boolean = false
)

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthYear: String,
    val category: String, // Vegetables, Fruits, Protein, Grains, Dairy, Pantry, Spices, Other
    val itemName: String,
    val quantity: String,
    val isChecked: Boolean = false
)

@Entity(tableName = "daily_checkins")
data class DailyCheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val mood: String, // Great, Good, Okay, Low, Tired
    val energyLevel: String, // Low, Okay, Good, Great
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val category: String, // Meal, Water, Weight, Monthly
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
