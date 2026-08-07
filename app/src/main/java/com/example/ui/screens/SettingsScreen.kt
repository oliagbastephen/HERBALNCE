package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.UserProfileEntity
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    profile: UserProfileEntity?,
    onNavigate: (Screen) -> Unit,
    onUpdateProfile: (UserProfileEntity) -> Unit
) {
    val user = profile ?: UserProfileEntity()
    var isGiftMode by remember { mutableStateOf(user.isGiftMode) }
    var unitSystem by remember { mutableStateOf(user.unitSystem) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Settings & Privacy",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = DisplayFontFamily,
                fontWeight = FontWeight.Bold,
                color = DeepForest
            )
        )

        // Preferences Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("App Preferences", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Units of Measurement", style = MaterialTheme.typography.bodyLarge)
                        Text("Kilograms (kg) & Liters (L)", style = MaterialTheme.typography.bodySmall, color = MutedText)
                    }
                    Switch(
                        checked = unitSystem == "Metric",
                        onCheckedChange = {
                            unitSystem = if (it) "Metric" else "Imperial"
                            onUpdateProfile(user.copy(unitSystem = unitSystem))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = DeepForest)
                    )
                }

                HorizontalDivider(color = SoftSand)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Birthday Gift Mode", style = MaterialTheme.typography.bodyLarge)
                        Text("Personalized birthday welcome message", style = MaterialTheme.typography.bodySmall, color = MutedText)
                    }
                    Switch(
                        checked = isGiftMode,
                        onCheckedChange = {
                            isGiftMode = it
                            onUpdateProfile(user.copy(isGiftMode = isGiftMode))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = DeepForest)
                    )
                }
            }
        }

        // Admin Access Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SoftSand),
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
                    Text("Admin Dashboard", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest))
                    Text("Manage system configurations and metrics", style = MaterialTheme.typography.bodySmall, color = DeepCharcoal)
                }
                Button(
                    onClick = { onNavigate(Screen.Admin) },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForest),
                    modifier = Modifier.testTag("open_admin_dashboard_button")
                ) {
                    Text("Open")
                }
            }
        }

        // Health Disclaimer Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Health & Medical Disclaimer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "HERBALANCE is a lifestyle and nutritional wellness companion designed for informational purposes only. It does not constitute medical advice or diagnose health conditions. Always consult a qualified physician or registered dietitian before making significant dietary changes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }
        }
    }
}
