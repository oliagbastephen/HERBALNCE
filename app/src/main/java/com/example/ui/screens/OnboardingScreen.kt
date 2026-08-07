package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    currentProfile: UserProfileEntity?,
    onCompleteOnboarding: (UserProfileEntity) -> Unit,
    onBackToLanding: () -> Unit
) {
    var step by remember { mutableStateOf(1) }

    // Form state
    var name by remember { mutableStateOf(currentProfile?.name ?: "Beautiful") }
    var age by remember { mutableStateOf((currentProfile?.age ?: 35).toString()) }
    var heightCm by remember { mutableStateOf((currentProfile?.heightCm ?: 168f).toInt().toString()) }
    var weightKg by remember { mutableStateOf((currentProfile?.weightKg ?: 68f).toInt().toString()) }
    var unitSystem by remember { mutableStateOf(currentProfile?.unitSystem ?: "Metric") }

    val selectedGoals = remember { mutableStateListOf<String>().apply {
        addAll((currentProfile?.goals ?: "Eat healthier, Improve energy").split(", "))
    } }

    var activityLevel by remember { mutableStateOf(currentProfile?.activityLevel ?: "Moderately active") }

    val selectedCuisines = remember { mutableStateListOf<String>().apply {
        addAll((currentProfile?.cuisinePreferences ?: "Nigerian, West African, Mediterranean").split(", "))
    } }

    var dietaryRestriction by remember { mutableStateOf(currentProfile?.dietaryRestrictions ?: "No restrictions") }

    val selectedAllergies = remember { mutableStateListOf<String>().apply {
        addAll((currentProfile?.allergies ?: "None").split(", ").filter { it.isNotBlank() })
    } }

    var mealsPerDay by remember { mutableStateOf(currentProfile?.mealsPerDay ?: 3) }
    var cookingTime by remember { mutableStateOf(currentProfile?.cookingTime ?: "30-45 mins") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Step $step of 7", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (step > 1) step-- else onBackToLanding()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmIvory)
            )
        },
        containerColor = WarmIvory
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = { step / 7f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = WarmTerracotta,
                trackColor = SoftSand
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                1 -> StepAboutYou(
                    name = name, onNameChange = { name = it },
                    age = age, onAgeChange = { age = it },
                    heightCm = heightCm, onHeightChange = { heightCm = it },
                    weightKg = weightKg, onWeightChange = { weightKg = it },
                    unitSystem = unitSystem, onUnitChange = { unitSystem = it }
                )
                2 -> StepGoals(selectedGoals)
                3 -> StepActivity(activityLevel) { activityLevel = it }
                4 -> StepCuisines(selectedCuisines)
                5 -> StepDietary(dietaryRestriction) { dietaryRestriction = it }
                6 -> StepAllergies(selectedAllergies)
                7 -> StepLifestyle(
                    mealsPerDay = mealsPerDay, onMealsChange = { mealsPerDay = it },
                    cookingTime = cookingTime, onTimeChange = { cookingTime = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation CTA
            Button(
                onClick = {
                    if (step < 7) {
                        step++
                    } else {
                        val parsedAge = age.toIntOrNull() ?: 35
                        val parsedHeight = heightCm.toFloatOrNull() ?: 168f
                        val parsedWeight = weightKg.toFloatOrNull() ?: 68f

                        val updatedProfile = (currentProfile ?: UserProfileEntity()).copy(
                            name = name,
                            age = parsedAge,
                            heightCm = parsedHeight,
                            weightKg = parsedWeight,
                            unitSystem = unitSystem,
                            goals = selectedGoals.joinToString(", "),
                            activityLevel = activityLevel,
                            cuisinePreferences = selectedCuisines.joinToString(", "),
                            dietaryRestrictions = dietaryRestriction,
                            allergies = if (selectedAllergies.isEmpty()) "None" else selectedAllergies.joinToString(", "),
                            mealsPerDay = mealsPerDay,
                            cookingTime = cookingTime,
                            onboardingCompleted = true
                        )
                        onCompleteOnboarding(updatedProfile)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepForest),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_next_button")
            ) {
                Text(
                    text = if (step < 7) "Continue" else "Build My Personalized Plan 🌿",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun StepAboutYou(
    name: String, onNameChange: (String) -> Unit,
    age: String, onAgeChange: (String) -> Unit,
    heightCm: String, onHeightChange: (String) -> Unit,
    weightKg: String, onWeightChange: (String) -> Unit,
    unitSystem: String, onUnitChange: (String) -> Unit
) {
    Text("About You", style = MaterialTheme.typography.headlineMedium, fontFamily = DisplayFontFamily, color = DeepForest)
    Text("Tell us a little about yourself to calculate your target estimates.", style = MaterialTheme.typography.bodyMedium, color = MutedText)

    Spacer(modifier = Modifier.height(20.dp))

    OutlinedTextField(
        value = name, onValueChange = onNameChange,
        label = { Text("First Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = age, onValueChange = onAgeChange,
        label = { Text("Age (years)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
    )
    Spacer(modifier = Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = heightCm, onValueChange = onHeightChange,
            label = { Text("Height (cm)") }, modifier = Modifier.weight(1f), singleLine = true
        )
        OutlinedTextField(
            value = weightKg, onValueChange = onWeightChange,
            label = { Text("Current Weight (kg)") }, modifier = Modifier.weight(1f), singleLine = true
        )
    }
}

@Composable
fun StepGoals(selectedGoals: MutableList<String>) {
    Text("Your Goal", style = MaterialTheme.typography.headlineMedium, fontFamily = DisplayFontFamily, color = DeepForest)
    Text("What would you like to work toward? Select all that apply.", style = MaterialTheme.typography.bodyMedium, color = MutedText)

    Spacer(modifier = Modifier.height(20.dp))

    val goals = listOf("Lose weight", "Maintain weight", "Gain weight", "Eat healthier", "Improve energy", "Build better eating habits")

    goals.forEach { goal ->
        val isSelected = selectedGoals.contains(goal)
        FilterChip(
            selected = isSelected,
            onClick = {
                if (isSelected) selectedGoals.remove(goal) else selectedGoals.add(goal)
            },
            label = { Text(goal) },
            leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, contentDescription = null) } } else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = SoftSand,
                selectedLabelColor = DeepForest
            )
        )
    }
}

@Composable
fun StepActivity(currentActivity: String, onSelectActivity: (String) -> Unit) {
    Text("Activity Level", style = MaterialTheme.typography.headlineMedium, fontFamily = DisplayFontFamily, color = DeepForest)
    Text("How active is your typical day?", style = MaterialTheme.typography.bodyMedium, color = MutedText)

    Spacer(modifier = Modifier.height(20.dp))

    val options = listOf(
        "Mostly sedentary" to "Desk job, minimal physical exertion",
        "Lightly active" to "Light walking, occasional workout 1-2x/week",
        "Moderately active" to "Active daily routine, exercise 3-4x/week",
        "Very active" to "Physical job or intense workouts 5+ days/week"
    )

    options.forEach { (title, desc) ->
        val isSelected = currentActivity == title
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = if (isSelected) SoftSand else Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable { onSelectActivity(title) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(desc, style = MaterialTheme.typography.bodySmall, color = MutedText)
            }
        }
    }
}

@Composable
fun StepCuisines(selectedCuisines: MutableList<String>) {
    Text("Food Preferences", style = MaterialTheme.typography.headlineMedium, fontFamily = DisplayFontFamily, color = DeepForest)
    Text("Which cuisines and flavors do you enjoy most?", style = MaterialTheme.typography.bodyMedium, color = MutedText)

    Spacer(modifier = Modifier.height(20.dp))

    val cuisines = listOf("Nigerian", "West African", "Mediterranean", "Mexican", "American", "Asian", "Vegetarian")

    cuisines.forEach { cuisine ->
        val isSelected = selectedCuisines.contains(cuisine)
        FilterChip(
            selected = isSelected,
            onClick = {
                if (isSelected) selectedCuisines.remove(cuisine) else selectedCuisines.add(cuisine)
            },
            label = { Text(cuisine) },
            leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, contentDescription = null) } } else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
fun StepDietary(currentRestriction: String, onSelect: (String) -> Unit) {
    Text("Dietary Restrictions", style = MaterialTheme.typography.headlineMedium, fontFamily = DisplayFontFamily, color = DeepForest)
    Text("Do you follow a specific dietary pattern?", style = MaterialTheme.typography.bodyMedium, color = MutedText)

    Spacer(modifier = Modifier.height(20.dp))

    val restrictions = listOf("No restrictions", "Vegetarian", "Vegan", "Pescatarian", "Gluten-free", "Low lactose")

    restrictions.forEach { res ->
        val isSelected = currentRestriction == res
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = if (isSelected) SoftSand else Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onSelect(res) }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(res, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                if (isSelected) Icon(Icons.Default.Check, contentDescription = null, tint = WarmTerracotta)
            }
        }
    }
}

@Composable
fun StepAllergies(selectedAllergies: MutableList<String>) {
    Text("Allergies (Hard Constraints)", style = MaterialTheme.typography.headlineMedium, fontFamily = DisplayFontFamily, color = DeepForest)
    Text("Gemini AI will strictly exclude these ingredients from your meal plans.", style = MaterialTheme.typography.bodyMedium, color = MutedText)

    Spacer(modifier = Modifier.height(20.dp))

    val allergiesList = listOf("Peanuts", "Tree nuts", "Eggs", "Milk", "Fish", "Shellfish", "Soy", "Wheat")

    allergiesList.forEach { allergy ->
        val isSelected = selectedAllergies.contains(allergy)
        FilterChip(
            selected = isSelected,
            onClick = {
                if (isSelected) selectedAllergies.remove(allergy) else selectedAllergies.add(allergy)
            },
            label = { Text(allergy) },
            leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, contentDescription = null) } } else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
fun StepLifestyle(
    mealsPerDay: Int, onMealsChange: (Int) -> Unit,
    cookingTime: String, onTimeChange: (String) -> Unit
) {
    Text("Lifestyle & Schedule", style = MaterialTheme.typography.headlineMedium, fontFamily = DisplayFontFamily, color = DeepForest)
    Text("Customize your daily routine preferences.", style = MaterialTheme.typography.bodyMedium, color = MutedText)

    Spacer(modifier = Modifier.height(20.dp))

    Text("Preferred meals per day:", style = MaterialTheme.typography.titleMedium)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
        listOf(2, 3, 4).forEach { count ->
            FilterChip(
                selected = mealsPerDay == count,
                onClick = { onMealsChange(count) },
                label = { Text("$count Meals") }
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text("Average cooking time:", style = MaterialTheme.typography.titleMedium)
    listOf("15-30 mins", "30-45 mins", "45+ mins").forEach { time ->
        val isSelected = cookingTime == time
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = if (isSelected) SoftSand else Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onTimeChange(time) }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(time, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                if (isSelected) Icon(Icons.Default.Check, contentDescription = null, tint = WarmTerracotta)
            }
        }
    }
}
