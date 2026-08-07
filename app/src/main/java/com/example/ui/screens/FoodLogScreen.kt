package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.FoodLogEntity
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.*

@Composable
fun FoodLogScreen(
    selectedDate: String,
    foodLogs: List<FoodLogEntity>,
    profile: UserProfileEntity?,
    onDateChange: (String) -> Unit,
    onLogFood: (String, String, Float, String, Int, Float, Float, Float, Float) -> Unit,
    onDeleteFood: (FoodLogEntity) -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }

    // Form inputs
    var mealType by remember { mutableStateOf("Breakfast") }
    var foodName by remember { mutableStateOf("") }
    var portionSize by remember { mutableStateOf("1") }
    var portionUnit by remember { mutableStateOf("serving") }
    var caloriesInput by remember { mutableStateOf("350") }
    var proteinInput by remember { mutableStateOf("20") }
    var carbsInput by remember { mutableStateOf("40") }
    var fatInput by remember { mutableStateOf("10") }
    var fiberInput by remember { mutableStateOf("6") }

    val totalCalories = foodLogs.sumOf { it.calories }
    val totalProtein = foodLogs.sumOf { it.protein.toDouble() }.toFloat()
    val totalCarbs = foodLogs.sumOf { it.carbs.toDouble() }.toFloat()
    val totalFat = foodLogs.sumOf { it.fat.toDouble() }.toFloat()
    val totalFiber = foodLogs.sumOf { it.fiber.toDouble() }.toFloat()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Previous Day */ }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day")
                    }
                    Text(
                        text = "Date: $selectedDate",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = { /* Next Day */ }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Day")
                    }
                }
            }
        }

        // Daily Macro Summary Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSand),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Daily Intake Summary",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = DeepForest
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroStat("Calories", "$totalCalories", "kcal", WarmTerracotta)
                        MacroStat("Protein", "${totalProtein.toInt()}", "g", DeepForest)
                        MacroStat("Carbs", "${totalCarbs.toInt()}", "g", DeepCharcoal)
                        MacroStat("Fat", "${totalFat.toInt()}", "g", MutedText)
                        MacroStat("Fiber", "${totalFiber.toInt()}", "g", SoftSage)
                    }
                }
            }
        }

        // Add Food CTA Button
        item {
            Button(
                onClick = { showAddForm = !showAddForm },
                colors = ButtonDefaults.buttonColors(containerColor = DeepForest),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("toggle_add_food_form")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (showAddForm) "Close Food Form" else "+ Log Food / Meal Entry")
            }
        }

        // Add Food Form
        if (showAddForm) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Log Food Entry", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { type ->
                                FilterChip(
                                    selected = mealType == type,
                                    onClick = { mealType = type },
                                    label = { Text(type, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = foodName, onValueChange = { foodName = it },
                            label = { Text("Food / Dish Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = portionSize, onValueChange = { portionSize = it },
                                label = { Text("Portion") }, modifier = Modifier.weight(1f), singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            OutlinedTextField(
                                value = portionUnit, onValueChange = { portionUnit = it },
                                label = { Text("Unit (g, bowl, slice)") }, modifier = Modifier.weight(1f), singleLine = true
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = caloriesInput, onValueChange = { caloriesInput = it },
                                label = { Text("Calories (kcal)") }, modifier = Modifier.weight(1f), singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = proteinInput, onValueChange = { proteinInput = it },
                                label = { Text("Protein (g)") }, modifier = Modifier.weight(1f), singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = carbsInput, onValueChange = { carbsInput = it },
                                label = { Text("Carbs (g)") }, modifier = Modifier.weight(1f), singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = fiberInput, onValueChange = { fiberInput = it },
                                label = { Text("Fiber (g)") }, modifier = Modifier.weight(1f), singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        Button(
                            onClick = {
                                if (foodName.isNotBlank()) {
                                    onLogFood(
                                        mealType,
                                        foodName,
                                        portionSize.toFloatOrNull() ?: 1f,
                                        portionUnit,
                                        caloriesInput.toIntOrNull() ?: 300,
                                        proteinInput.toFloatOrNull() ?: 15f,
                                        carbsInput.toFloatOrNull() ?: 35f,
                                        fatInput.toFloatOrNull() ?: 10f,
                                        fiberInput.toFloatOrNull() ?: 5f
                                    )
                                    foodName = ""
                                    showAddForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WarmTerracotta),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Food Entry")
                        }
                    }
                }
            }
        }

        // Logged Foods Header
        item {
            Text(
                text = "Logged Items for $selectedDate",
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = DisplayFontFamily, fontWeight = FontWeight.Bold, color = DeepForest)
            )
        }

        if (foodLogs.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No food items logged for this date yet.", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                    }
                }
            }
        } else {
            items(foodLogs) { log ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(log.mealType, style = MaterialTheme.typography.labelSmall) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = SoftSand)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${log.calories} kcal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(log.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                            Text("Portion: ${log.portionSize} ${log.portionUnit} | Protein: ${log.protein}g | Fiber: ${log.fiber}g", style = MaterialTheme.typography.bodySmall, color = MutedText)
                        }

                        IconButton(onClick = { onDeleteFood(log) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete entry", tint = MutedText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MacroStat(label: String, value: String, unit: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = color))
        Text(unit, style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    }
}
