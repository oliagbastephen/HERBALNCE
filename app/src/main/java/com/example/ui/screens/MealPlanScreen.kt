package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.MealPlanEntity
import com.example.ui.theme.*

@Composable
fun MealPlanScreen(
    currentMonth: String,
    mealPlans: List<MealPlanEntity>,
    isGenerating: Boolean,
    onGenerateFreshPlan: () -> Unit,
    onOpenSwap: (MealPlanEntity) -> Unit,
    onLogMeal: (MealPlanEntity) -> Unit
) {
    var selectedWeek by remember { mutableStateOf(1) }

    val weekMeals = mealPlans.filter { it.weekNumber == selectedWeek || mealPlans.size <= 4 }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month Title & Regenerate CTA
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your $currentMonth Plan",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = DeepForest
                        )
                    )
                    Text(
                        text = "Gemini AI personalized monthly schedule",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText
                    )
                }

                Button(
                    onClick = onGenerateFreshPlan,
                    colors = ButtonDefaults.buttonColors(containerColor = WarmTerracotta),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("regenerate_plan_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Refresh Plan")
                }
            }
        }

        // Loading Skeleton state
        if (isGenerating) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = WarmTerracotta)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Gemini AI is crafting your personalized monthly plan...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Matching allergies, goals, and cultural taste preferences",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                }
            }
        }

        // Week Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedWeek - 1,
                containerColor = WarmIvory,
                contentColor = DeepForest,
                edgePadding = 0.dp
            ) {
                (1..4).forEach { w ->
                    Tab(
                        selected = selectedWeek == w,
                        onClick = { selectedWeek = w },
                        text = {
                            Text(
                                "Week $w",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (selectedWeek == w) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }
        }

        // Meals List
        if (!isGenerating && weekMeals.isEmpty()) {
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
                        Text("No meal items for Week $selectedWeek yet.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onGenerateFreshPlan, colors = ButtonDefaults.buttonColors(containerColor = DeepForest)) {
                            Text("Generate Week $selectedWeek Plan")
                        }
                    }
                }
            }
        } else if (!isGenerating) {
            items(weekMeals) { meal ->
                MealCardItem(
                    meal = meal,
                    onViewRecipe = { },
                    onLogMeal = { onLogMeal(meal) },
                    onSwap = { onOpenSwap(meal) }
                )
            }
        }
    }
}
