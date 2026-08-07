package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.DailyCheckInEntity
import com.example.ui.theme.*

@Composable
fun DailyCheckInScreen(
    currentCheckIn: DailyCheckInEntity?,
    onSaveCheckIn: (String, String, String) -> Unit
) {
    var selectedMood by remember { mutableStateOf(currentCheckIn?.mood ?: "Good") }
    var selectedEnergy by remember { mutableStateOf(currentCheckIn?.energyLevel ?: "Good") }
    var noteInput by remember { mutableStateOf(currentCheckIn?.note ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Daily Wellness Check-In",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = DisplayFontFamily,
                fontWeight = FontWeight.Bold,
                color = DeepForest
            )
        )
        Text(
            text = "How are you feeling today physically and mentally?",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText
        )

        // Mood Selection
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Overall Mood", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(12.dp))

                val moods = listOf("Great", "Good", "Okay", "Low", "Tired")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    moods.forEach { mood ->
                        FilterChip(
                            selected = selectedMood == mood,
                            onClick = { selectedMood = mood },
                            label = { Text(mood) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SoftSand,
                                selectedLabelColor = DeepForest
                            )
                        )
                    }
                }
            }
        }

        // Energy Selection
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Energy Level", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(12.dp))

                val energies = listOf("Low", "Okay", "Good", "Great")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    energies.forEach { energy ->
                        FilterChip(
                            selected = selectedEnergy == energy,
                            onClick = { selectedEnergy = energy },
                            label = { Text(energy) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SoftSand,
                                selectedLabelColor = DeepForest
                            )
                        )
                    }
                }
            }
        }

        // Notes Input
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Reflection & Notes", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    placeholder = { Text("How was your sleep, appetite, or focus today?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
        }

        Button(
            onClick = { onSaveCheckIn(selectedMood, selectedEnergy, noteInput) },
            colors = ButtonDefaults.buttonColors(containerColor = DeepForest),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_checkin_button")
        ) {
            Text("Save Daily Reflection", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
        }
    }
}
