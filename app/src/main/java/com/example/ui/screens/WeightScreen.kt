package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.WeightLogEntity
import com.example.ui.theme.*

@Composable
fun WeightScreen(
    weightLogs: List<WeightLogEntity>,
    onOpenLogWeight: () -> Unit
) {
    val currentWeight = weightLogs.lastOrNull()?.weightKg ?: 68f
    val startingWeight = weightLogs.firstOrNull()?.weightKg ?: currentWeight
    val diff = currentWeight - startingWeight

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Weight & Body Progress",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                )
            )
            Text(
                text = "Weight is just one data point among many. We celebrate overall energy and strength.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )
        }

        // Current Weight Summary Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Weight", style = MaterialTheme.typography.labelMedium, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currentWeight kg",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepForest
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (diff <= 0) "${String.format("%.1f", -diff)} kg total progress" else "+${String.format("%.1f", diff)} kg change",
                            style = MaterialTheme.typography.labelMedium,
                            color = SoftSage,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onOpenLogWeight,
                        colors = ButtonDefaults.buttonColors(containerColor = WarmTerracotta),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("weight_screen_log_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Entry")
                    }
                }
            }
        }

        // Visual Trend Canvas Chart
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSand),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Weight Trend",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (weightLogs.size < 2) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Log at least 2 entries to view your trend line.", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                        }
                    } else {
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            val weights = weightLogs.map { it.weightKg }
                            val maxW = weights.maxOrNull() ?: 100f
                            val minW = weights.minOrNull() ?: 50f
                            val range = (maxW - minW).coerceAtLeast(1f)

                            val path = Path()
                            val width = size.width
                            val height = size.height

                            weights.forEachIndexed { index, w ->
                                val x = index * (width / (weights.size - 1))
                                val y = height - ((w - minW) / range) * (height * 0.7f) - (height * 0.15f)
                                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }

                            drawPath(
                                path = path,
                                color = DeepForest,
                                style = Stroke(width = 6.dp.toPx())
                            )
                        }
                    }
                }
            }
        }

        // Weight History Log List
        item {
            Text(
                text = "History Logs",
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = DisplayFontFamily, fontWeight = FontWeight.Bold, color = DeepForest)
            )
        }

        items(weightLogs.reversed()) { log ->
            Card(
                shape = RoundedCornerShape(12.dp),
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
                    Column {
                        Text(log.date, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        if (log.note.isNotBlank()) {
                            Text(log.note, style = MaterialTheme.typography.bodySmall, color = MutedText)
                        }
                    }
                    Text("${log.weightKg} kg", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = DeepForest))
                }
            }
        }
    }
}
