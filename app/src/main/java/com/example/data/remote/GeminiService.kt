package com.example.data.remote

import com.example.BuildConfig
import com.example.data.local.MealPlanEntity
import com.example.data.local.UserProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null
)

@Serializable
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

@Serializable
data class AiMealPlanItem(
    val weekNumber: Int,
    val dayOfWeek: String,
    val mealType: String,
    val mealName: String,
    val description: String,
    val calories: Int,
    val protein: Int,
    val fiber: Int,
    val prepTimeMinutes: Int
)

@Serializable
data class AiMealSwapItem(
    val mealName: String,
    val description: String,
    val calories: Int,
    val protein: Int,
    val fiber: Int,
    val prepTimeMinutes: Int
)

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val json = Json { ignoreUnknownKeys = true }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Generates a dynamic 4-week monthly meal plan based on user profile.
     */
    suspend fun generateMonthlyMealPlan(
        profile: UserProfileEntity,
        monthYear: String
    ): List<MealPlanEntity> = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackMonthlyMealPlan(monthYear, profile)
        }

        val prompt = """
            Create a personalized monthly meal plan for a woman with the following profile:
            - Age: ${profile.age}
            - Current Weight: ${profile.weightKg} kg
            - Goals: ${profile.goals}
            - Activity Level: ${profile.activityLevel}
            - Cuisine Preferences: ${profile.cuisinePreferences}
            - Dietary Restrictions: ${profile.dietaryRestrictions}
            - Allergies (HARD CONSTRAINT): ${profile.allergies}
            - Daily Calorie Target: ${profile.calorieTarget} kcal
            - Meals per day: ${profile.mealsPerDay}
            - Cooking Time: ${profile.cookingTime}
            
            Return a JSON array of 12 meal items (for Week 1 & Week 2 across Monday to Thursday for Breakfast, Lunch, Dinner).
            Each JSON object MUST contain:
            "weekNumber" (Int: 1 or 2),
            "dayOfWeek" (String: "Monday", "Tuesday", "Wednesday", "Thursday"),
            "mealType" (String: "Breakfast", "Lunch", "Dinner"),
            "mealName" (String),
            "description" (String),
            "calories" (Int),
            "protein" (Int),
            "fiber" (Int),
            "prepTimeMinutes" (Int)
            
            DO NOT recommend starvation or extreme restrictions. Respect allergies strictly. Include culturally relevant dishes when applicable.
        """.trimIndent()

        try {
            val reqBody = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(
                    responseMimeType = "application/json",
                    temperature = 0.7f
                )
            )

            val jsonString = json.encodeToString(GenerateContentRequest.serializer(), reqBody)
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonString.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseText = response.body?.string() ?: ""

            if (!response.isSuccessful || responseText.isBlank()) {
                return@withContext getFallbackMonthlyMealPlan(monthYear, profile)
            }

            val parsedResponse = json.decodeFromString(GenerateContentResponse.serializer(), responseText)
            val rawJsonText = parsedResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""

            if (rawJsonText.isNotBlank()) {
                val cleanedText = cleanJsonText(rawJsonText)
                val aiItems = json.decodeFromString<List<AiMealPlanItem>>(cleanedText)
                return@withContext aiItems.map { item ->
                    MealPlanEntity(
                        monthYear = monthYear,
                        weekNumber = item.weekNumber,
                        dayOfWeek = item.dayOfWeek,
                        mealType = item.mealType,
                        mealName = item.mealName,
                        calories = item.calories,
                        protein = item.protein,
                        fiber = item.fiber,
                        prepTimeMinutes = item.prepTimeMinutes,
                        description = item.description
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        getFallbackMonthlyMealPlan(monthYear, profile)
    }

    /**
     * Generates 3 AI alternatives for swapping a meal.
     */
    suspend fun generateMealSwaps(
        originalMealName: String,
        profile: UserProfileEntity
    ): List<AiMealSwapItem> = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackSwaps(originalMealName, profile)
        }

        val prompt = """
            The user wants to swap out the meal "$originalMealName".
            User profile:
            - Dietary Restrictions: ${profile.dietaryRestrictions}
            - Allergies: ${profile.allergies}
            - Cuisine Preferences: ${profile.cuisinePreferences}
            - Target Calorie range: ~${profile.calorieTarget / 3} kcal per main meal.
            
            Provide 3 nutritious alternatives respecting allergies and preferences.
            Return a JSON array of 3 objects with fields: "mealName", "description", "calories", "protein", "fiber", "prepTimeMinutes".
        """.trimIndent()

        try {
            val reqBody = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(
                    responseMimeType = "application/json",
                    temperature = 0.7f
                )
            )

            val jsonString = json.encodeToString(GenerateContentRequest.serializer(), reqBody)
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonString.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseText = response.body?.string() ?: ""

            if (response.isSuccessful && responseText.isNotBlank()) {
                val parsed = json.decodeFromString(GenerateContentResponse.serializer(), responseText)
                val rawText = parsed.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                if (rawText.isNotBlank()) {
                    val cleanedText = cleanJsonText(rawText)
                    return@withContext json.decodeFromString<List<AiMealSwapItem>>(cleanedText)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        getFallbackSwaps(originalMealName, profile)
    }

    /**
     * Interactive Voice Wellness Companion responding in spoken-friendly natural language.
     */
    suspend fun chatVoiceCompanion(
        userPrompt: String,
        profile: UserProfileEntity
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Hello! I am your HERBALANCE Voice Companion. Based on your goal (${profile.goals}), remember that balanced meals rich in protein, healthy fats, and fiber nourish both mind and body. How can I assist you today?"
        }

        val systemPrompt = """
            You are the HERBALANCE AI Voice Wellness Companion for a woman aged ${profile.age}.
            User details:
            - Goals: ${profile.goals}
            - Dietary Restrictions: ${profile.dietaryRestrictions}
            - Allergies: ${profile.allergies}
            - Daily Calorie Target: ${profile.calorieTarget} kcal
            
            Keep your answer conversational, warm, highly supportive, clear, and concise (2-4 sentences max), formatted perfectly for voice synthesis / speaking out loud.
        """.trimIndent()

        val fullPrompt = "$systemPrompt\n\nUser Question: $userPrompt"

        try {
            val reqBody = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = fullPrompt)))),
                generationConfig = GenerationConfig(temperature = 0.7f)
            )

            val jsonString = json.encodeToString(GenerateContentRequest.serializer(), reqBody)
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonString.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseText = response.body?.string() ?: ""

            if (response.isSuccessful && responseText.isNotBlank()) {
                val parsed = json.decodeFromString(GenerateContentResponse.serializer(), responseText)
                val text = parsed.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                if (text.isNotBlank()) return@withContext text.trim()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        "I am here to support your nutrition and wellness journey. Drinking enough water and prioritizing fresh, whole foods will keep your energy vibrant today!"
    }

    private fun cleanJsonText(rawText: String): String {
        var text = rawText.trim()
        if (text.startsWith("```json")) {
            text = text.removePrefix("```json").trim()
        } else if (text.startsWith("```")) {
            text = text.removePrefix("```").trim()
        }
        if (text.endsWith("```")) {
            text = text.removeSuffix("```").trim()
        }
        return text
    }

    /**
     * Generates a daily gentle nutrition tip or hydration insight.
     */
    suspend fun generateDailyTip(profile: UserProfileEntity, waterLoggedMl: Int): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext if (waterLoggedMl < 1000) {
                "Hydration Check: You're a little behind your usual water intake today. A refreshing glass now can help boost your focus and energy. 💧"
            } else {
                "Today's Tip: Pairing complex carbohydrates with lean protein and fiber helps maintain steady blood sugar and consistent energy levels throughout your afternoon."
            }
        }

        val prompt = "Give a short, gentle, encouraging 2-sentence nutrition or wellness tip for a 35-year-old woman whose goal is '${profile.goals}'. Keep it friendly, non-judgmental, and practical."

        try {
            val reqBody = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt))))
            )
            val jsonString = json.encodeToString(GenerateContentRequest.serializer(), reqBody)
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonString.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseText = response.body?.string() ?: ""
            if (response.isSuccessful && responseText.isNotBlank()) {
                val parsed = json.decodeFromString(GenerateContentResponse.serializer(), responseText)
                val text = parsed.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                if (text.isNotBlank()) return@withContext text.trim()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        "Today's Tip: Try incorporating leafy greens and vibrant berries to support cellular recovery and vibrant daily energy!"
    }

    // --- Fallback Data Generators ---
    private fun getFallbackMonthlyMealPlan(monthYear: String, profile: UserProfileEntity): List<MealPlanEntity> {
        val isWestAfrican = profile.cuisinePreferences.contains("Nigerian") || profile.cuisinePreferences.contains("West African")
        
        return listOf(
            // Week 1
            MealPlanEntity(monthYear = monthYear, weekNumber = 1, dayOfWeek = "Monday", mealType = "Breakfast", mealName = if (isWestAfrican) "Akara (Bean Cakes) & Pap with Berries" else "Warm Berry Cinnamon Oatmeal Bowl", calories = 380, protein = 16, fiber = 7, prepTimeMinutes = 20, description = "Protein-rich, comforting breakfast cooked in gentle olive oil."),
            MealPlanEntity(monthYear = monthYear, weekNumber = 1, dayOfWeek = "Monday", mealType = "Lunch", mealName = if (isWestAfrican) "Steamed Moi Moi & Garden Salad" else "Mediterranean Quinoa & Chickpea Power Bowl", calories = 520, protein = 24, fiber = 11, prepTimeMinutes = 25, description = "Nutrient-dense plant protein paired with fresh crisp greens."),
            MealPlanEntity(monthYear = monthYear, weekNumber = 1, dayOfWeek = "Monday", mealType = "Dinner", mealName = if (isWestAfrican) "Grilled Tilapia with Roasted Plantain & Spinach" else "Lemon Herb Grilled Salmon with Sweet Potato & Asparagus", calories = 580, protein = 42, fiber = 8, prepTimeMinutes = 35, description = "Omega-3 rich fish balanced with complex carbs and vital greens."),
            MealPlanEntity(monthYear = monthYear, weekNumber = 1, dayOfWeek = "Monday", mealType = "Snack", mealName = "Greek Yogurt with Honey & Walnuts", calories = 210, protein = 12, fiber = 2, prepTimeMinutes = 5, description = "Creamy probiotic snack for gut health."),

            MealPlanEntity(monthYear = monthYear, weekNumber = 1, dayOfWeek = "Tuesday", mealType = "Breakfast", mealName = "Avocado & Poached Egg Toast", calories = 410, protein = 18, fiber = 8, prepTimeMinutes = 15, description = "Healthy fats and quality choline for brain focus."),
            MealPlanEntity(monthYear = monthYear, weekNumber = 1, dayOfWeek = "Tuesday", mealType = "Lunch", mealName = if (isWestAfrican) "Ofada Rice with Mild Peppers & Eggplant" else "Grilled Chicken Caesar Salad Wrap", calories = 540, protein = 35, fiber = 6, prepTimeMinutes = 25, description = "Unpolished nutrient-rich rice served with savory vegetable sauce."),
            MealPlanEntity(monthYear = monthYear, weekNumber = 1, dayOfWeek = "Tuesday", mealType = "Dinner", mealName = if (isWestAfrican) "Savor Stewed Brown Beans & Baked Sweet Potato" else "Tofu & Mixed Veggies Stir-Fry with Brown Rice", calories = 510, protein = 26, fiber = 14, prepTimeMinutes = 30, description = "Fiber powerhouse dinner supporting digestion and sustained energy."),
            MealPlanEntity(monthYear = monthYear, weekNumber = 1, dayOfWeek = "Tuesday", mealType = "Snack", mealName = "Sliced Apple with Almond Butter", calories = 190, protein = 5, fiber = 4, prepTimeMinutes = 5, description = "Crisp fiber with healthy plant fats."),

            // Week 2
            MealPlanEntity(monthYear = monthYear, weekNumber = 2, dayOfWeek = "Wednesday", mealType = "Breakfast", mealName = "Green Goddess Protein Smoothie Bowl", calories = 350, protein = 22, fiber = 8, prepTimeMinutes = 10, description = "Blended spinach, banana, chia seeds, and plant protein."),
            MealPlanEntity(monthYear = monthYear, weekNumber = 2, dayOfWeek = "Wednesday", mealType = "Lunch", mealName = "Roasted Turkey & Quinoa Buddha Bowl", calories = 490, protein = 38, fiber = 9, prepTimeMinutes = 20, description = "Balanced energy meal with complex grains and lean protein."),
            MealPlanEntity(monthYear = monthYear, weekNumber = 2, dayOfWeek = "Wednesday", mealType = "Dinner", mealName = if (isWestAfrican) "Peanut Egusi Soup with Goat Meat & Cauliflower Swallow" else "Baked Chicken Breast with Garlic Roasted Vegetables", calories = 560, protein = 40, fiber = 7, prepTimeMinutes = 40, description = "Comforting traditional soup rich in micronutrients and healthy fats."),
            MealPlanEntity(monthYear = monthYear, weekNumber = 2, dayOfWeek = "Wednesday", mealType = "Snack", mealName = "Mixed Berry & Seed Cluster Pot", calories = 160, protein = 6, fiber = 5, prepTimeMinutes = 5, description = "Antioxidant booster for mid-afternoon energy.")
        )
    }

    private fun getFallbackSwaps(originalMealName: String, profile: UserProfileEntity): List<AiMealSwapItem> {
        return listOf(
            AiMealSwapItem(
                mealName = "Pan-Seared Sea Bass with Roasted Asparagus & Yam",
                description = "Light, elegant white fish with low-glycemic roasted yam spears.",
                calories = 490,
                protein = 36,
                fiber = 6,
                prepTimeMinutes = 25
            ),
            AiMealSwapItem(
                mealName = "Hearty Black Bean & Sweet Potato Power Bowl",
                description = "Plant-based fiber champion packed with iron and magnesium.",
                calories = 460,
                protein = 20,
                fiber = 12,
                prepTimeMinutes = 20
            ),
            AiMealSwapItem(
                mealName = "Lemon Thyme Chicken Breast with Wild Rice Salad",
                description = "Crisp, flavorful lean protein paired with earthy wild rice.",
                calories = 520,
                protein = 42,
                fiber = 5,
                prepTimeMinutes = 30
            )
        )
    }
}
