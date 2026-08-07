package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.FoodLogEntity
import com.example.data.local.MealPlanEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WeightLogEntity
import com.example.ui.components.ProgressRing
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    profile: UserProfileEntity?,
    foodLogs: List<FoodLogEntity>,
    totalWaterMl: Int,
    weightLogs: List<WeightLogEntity>,
    mealPlan: List<MealPlanEntity>,
    dailyTip: String,
    onNavigate: (Screen) -> Unit,
    onOpenLogWater: () -> Unit,
    onOpenLogWeight: () -> Unit,
    onOpenMealSwap: (MealPlanEntity) -> Unit,
    onQuickLogMeal: (MealPlanEntity) -> Unit
) {
    val userName = profile?.name ?: "Beautiful"
    val calorieTarget = profile?.calorieTarget ?: 1850
    val waterTarget = profile?.waterTargetMl ?: 2000

    val consumedCalories = foodLogs.sumOf { it.calories }
    val remainingCalories = (calorieTarget - consumedCalories).coerceAtLeast(0)
    val calorieProgress = (consumedCalories.toFloat() / calorieTarget.toFloat()).coerceIn(0f, 1f)

    val currentWeight = weightLogs.lastOrNull()?.weightKg ?: profile?.weightKg ?: 68f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Greeting
        item {
            Column {
                Text(
                    text = "Good morning, $userName",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = DeepForest
                    )
                )
                Text(
                    text = "Let's make today a nourishing one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }
        }

        // 2. Calories Progress Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProgressRing(
                        progress = calorieProgress,
                        valueText = "$consumedCalories",
                        targetText = "$calorieTarget",
                        unitText = "kcal",
                        ringColor = WarmTerracotta,
                        trackColor = SoftSand
                    )

                    Column(
                        modifier = Modifier.padding(start = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$remainingCalories kcal",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepForest
                            )
                        )
                        Text(
                            text = "Remaining Today",
                            style = MaterialTheme.typography.labelMedium,
                            color = MutedText
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = { onNavigate(Screen.FoodLog) },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepForest),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("dashboard_log_food_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Food")
                        }
                    }
                }
            }
        }

        // 3. Water Tracker Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSand.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(WarmTerracotta.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = WarmTerracotta)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Today's Water",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${String.format("%.2f", totalWaterMl / 1000f)}L / ${String.format("%.1f", waterTarget / 1000f)}L",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedText
                                )
                            }
                        }

                        IconButton(onClick = onOpenLogWater) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Add Water", tint = WarmTerracotta)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { (totalWaterMl.toFloat() / waterTarget.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = WarmTerracotta,
                        trackColor = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = false,
                            onClick = onOpenLogWater,
                            label = { Text("+250 ml") },
                            shape = CircleShape
                        )
                        FilterChip(
                            selected = false,
                            onClick = onOpenLogWater,
                            label = { Text("+500 ml") },
                            shape = CircleShape
                        )
                        FilterChip(
                            selected = false,
                            onClick = onOpenLogWater,
                            label = { Text("Custom") },
                            shape = CircleShape
                        )
                    }
                }
            }
        }

        // 4. Quick Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionButton("Food Log", Icons.Outlined.MenuBook) { onNavigate(Screen.FoodLog) }
                QuickActionButton("Voice AI", Icons.Outlined.Mic) { onNavigate(Screen.VoiceConversation) }
                QuickActionButton("Water", Icons.Outlined.WaterDrop) { onOpenLogWater() }
                QuickActionButton("Weight", Icons.Outlined.MonitorWeight) { onOpenLogWeight() }
            }
        }

        // 5. Daily Insight Tip
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSage.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(Screen.VoiceConversation) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = DeepForest,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = dailyTip,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DeepForest,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "Tap to talk with Voice Assistant ➔",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = WarmTerracotta,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // 6. Today's Meals Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Meals",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = DeepForest
                    )
                )
                TextButton(onClick = { onNavigate(Screen.Meals) }) {
                    Text("See Monthly Plan", color = WarmTerracotta)
                }
            }
        }

        val todayMeals = mealPlan.take(4)
        if (todayMeals.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Preparing your personalized meal plan...", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onNavigate(Screen.Meals) },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepForest)
                        ) {
                            Text("View Meals")
                        }
                    }
                }
            }
        } else {
            items(todayMeals) { meal ->
                MealCardItem(
                    meal = meal,
                    onViewRecipe = { onNavigate(Screen.Recipes) },
                    onLogMeal = { onQuickLogMeal(meal) },
                    onSwap = { onOpenMealSwap(meal) }
                )
            }
        }

        // 7. Weight Overview Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Weight", style = MaterialTheme.typography.labelMedium, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currentWeight kg",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepForest
                            )
                        )
                        Text("Progressing steadily", style = MaterialTheme.typography.labelSmall, color = SoftSage)
                    }

                    Button(
                        onClick = onOpenLogWeight,
                        colors = ButtonDefaults.buttonColors(containerColor = WarmTerracotta),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("dashboard_log_weight_button")
                    ) {
                        Text("Log Weight")
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = DeepForest)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(title, style = MaterialTheme.typography.labelMedium, color = DeepCharcoal)
    }
}

@Composable
fun MealCardItem(
    meal: MealPlanEntity,
    onViewRecipe: () -> Unit,
    onLogMeal: () -> Unit,
    onSwap: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SuggestionChip(
                    onClick = { },
                    label = { Text(meal.mealType, style = MaterialTheme.typography.labelMedium) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = SoftSand)
                )
                Text(
                    text = "${meal.prepTimeMinutes} mins prep",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = meal.mealName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest)
            )

            if (meal.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meal.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("${meal.calories} kcal", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text("${meal.protein}g Protein", style = MaterialTheme.typography.labelMedium, color = WarmTerracotta)
                Text("${meal.fiber}g Fiber", style = MaterialTheme.typography.labelMedium, color = SoftSage)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onSwap,
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Swap", style = MaterialTheme.typography.labelMedium, color = MutedText)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onLogMeal,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForest),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("Log Meal", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
