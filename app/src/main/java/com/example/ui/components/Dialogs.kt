package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.local.MealPlanEntity
import com.example.data.remote.AiMealSwapItem
import com.example.ui.theme.*

@Composable
fun BirthdayWelcomeDialog(
    recipientName: String,
    birthdayMsg: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = WarmIvory,
            shadowElevation = 12.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .height(160.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_welcome_banner_1786142241853),
                        contentDescription = "Welcome Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Happy Birthday, $recipientName ❤️",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = DeepForest
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = birthdayMsg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeepCharcoal,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = WarmTerracotta),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("birthday_begin_button")
                ) {
                    Text("Let's Begin", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
                }
            }
        }
    }
}

@Composable
fun LogWaterDialog(
    onDismiss: () -> Unit,
    onAddWater: (Int) -> Unit
) {
    var customAmount by remember { mutableStateOf("250") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = WarmTerracotta)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Hydration", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column {
                Text("Select quick portion or enter custom amount:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(250, 500, 750).forEach { amount ->
                        FilterChip(
                            selected = customAmount == amount.toString(),
                            onClick = { customAmount = amount.toString() },
                            label = { Text("+$amount ml") },
                            shape = CircleShape,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { customAmount = it },
                    label = { Text("Custom Amount (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = customAmount.toIntOrNull() ?: 250
                    onAddWater(amount)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepForest)
            ) {
                Text("+ Add Water")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedText)
            }
        },
        containerColor = WarmIvory
    )
}

@Composable
fun LogWeightDialog(
    currentWeight: Float,
    onDismiss: () -> Unit,
    onSaveWeight: (Float, String) -> Unit
) {
    var weightInput by remember { mutableStateOf(currentWeight.toString()) }
    var noteInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Weight", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Note (optional, e.g., morning weigh-in)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightInput.toFloatOrNull() ?: currentWeight
                    onSaveWeight(w, noteInput)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepForest)
            ) {
                Text("Save Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedText)
            }
        },
        containerColor = WarmIvory
    )
}

@Composable
fun MealSwapDialog(
    originalMeal: MealPlanEntity,
    alternatives: List<AiMealSwapItem>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSelectSwap: (AiMealSwapItem) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = WarmIvory,
            shadowElevation = 8.dp,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WarmTerracotta)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Swap Meal",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = DeepForest
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Original: ${originalMeal.mealName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedText
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = WarmTerracotta)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Gemini is curating allergy-safe alternatives...", style = MaterialTheme.typography.bodyMedium)
                    }
                } else if (alternatives.isEmpty()) {
                    Text("No alternatives loaded yet.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.heightIn(max = 360.dp)
                    ) {
                        items(alternatives) { item ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectSwap(item) }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = item.mealName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedText
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text("${item.calories} kcal", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = DeepForest)
                                        Text("${item.protein}g Protein", style = MaterialTheme.typography.labelMedium, color = WarmTerracotta)
                                        Text("${item.fiber}g Fiber", style = MaterialTheme.typography.labelMedium, color = SoftSage)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
