package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.*

@Composable
fun WaterScreen(
    totalWaterMl: Int,
    profile: UserProfileEntity?,
    onAddWater: (Int) -> Unit
) {
    val targetMl = profile?.waterTargetMl ?: 2000
    val progress = (totalWaterMl.toFloat() / targetMl.toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Hydration Tracker",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                )
            )
            Text(
                text = "Consistent water intake supports metabolism, skin vibrancy, and focus.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )
        }

        // Bottle Visual Progress
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(WarmTerracotta.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = WarmTerracotta,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${String.format("%.2f", totalWaterMl / 1000f)} / ${String.format("%.1f", targetMl / 1000f)} Liters",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepForest
                        )
                    )

                    Text(
                        text = "$percentage% of daily hydration target met",
                        style = MaterialTheme.typography.labelLarge,
                        color = WarmTerracotta
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp)),
                        color = WarmTerracotta,
                        trackColor = SoftSand
                    )
                }
            }
        }

        // Quick Add Buttons
        item {
            Text(
                text = "Quick Log Portion",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WaterQuickButton("Cup", "250 ml", Modifier.weight(1f)) { onAddWater(250) }
                WaterQuickButton("Bottle", "500 ml", Modifier.weight(1f)) { onAddWater(500) }
                WaterQuickButton("Flask", "750 ml", Modifier.weight(1f)) { onAddWater(750) }
            }
        }

        // Hydration Benefits
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSand),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Gentle Hydration Reminder",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sipping water regularly throughout your day helps prevent brain fatigue, improves nutrient transport, and keeps digestion smooth without overburdening your kidneys.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeepCharcoal
                    )
                }
            }
        }
    }
}

@Composable
fun WaterQuickButton(label: String, amount: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .testTag("add_water_$amount")
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Add, contentDescription = null, tint = DeepForest)
                Spacer(modifier = Modifier.height(4.dp))
                Text(label, style = MaterialTheme.typography.labelMedium, color = DeepForest, fontWeight = FontWeight.Bold)
                Text(amount, style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
        }
    }
}
