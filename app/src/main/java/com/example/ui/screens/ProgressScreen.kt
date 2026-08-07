package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun ProgressScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Progress & Habit Streaks",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                )
            )
            Text(
                text = "Small, consistent daily habits build lasting wellness momentum.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )
        }

        // Streak Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftSand),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Hydration Streak", style = MaterialTheme.typography.labelMedium, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("5 Days 💧", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = DeepForest))
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftSage.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Logging Habits", style = MaterialTheme.typography.labelMedium, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("24 / 30 Days 🔥", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = DeepForest))
                    }
                }
            }
        }

        // Badges Section
        item {
            Text(
                text = "Wellness Milestones",
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = DisplayFontFamily, fontWeight = FontWeight.Bold, color = DeepForest)
            )
        }

        val badges = listOf(
            Triple("💧 Hydration Champion", "Reached 2.0L water goal for 5 days in a row", true),
            Triple("🥗 Balanced Plate", "Included protein, fiber & greens in 3 consecutive meals", true),
            Triple("🌱 Fresh Start", "Created personalized monthly AI meal plan", true),
            Triple("🔥 Consistency Icon", "Logged food entries for 20+ days this month", true)
        )

        badges.forEach { (title, desc, isEarned) ->
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isEarned) WarmTerracotta.copy(alpha = 0.2f) else SoftSand),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = if (isEarned) WarmTerracotta else MutedText)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest))
                            Text(desc, style = MaterialTheme.typography.bodySmall, color = DeepCharcoal)
                        }
                    }
                }
            }
        }
    }
}
