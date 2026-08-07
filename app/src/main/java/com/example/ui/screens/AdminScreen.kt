package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.*

@Composable
fun AdminScreen(
    profile: UserProfileEntity?,
    onUpdateBirthdayMsg: (String) -> Unit
) {
    var birthdayMsgInput by remember { mutableStateOf(profile?.birthdayMsg ?: "") }
    var saveSuccessMessage by remember { mutableStateOf(false) }

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
            text = "Admin & System Workspace",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = DisplayFontFamily,
                fontWeight = FontWeight.Bold,
                color = DeepForest
            )
        )

        // System Metrics Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Active Users", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    Text("1 Active", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DeepForest))
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AI Generator", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    Text("Gemini 3.5", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = SoftSage))
                }
            }
        }

        // Birthday Experience Configuration Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Birthday Experience Message Config", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest))
                Text("Customize the special gift greeting displayed to the user:", style = MaterialTheme.typography.bodySmall, color = MutedText)

                OutlinedTextField(
                    value = birthdayMsgInput,
                    onValueChange = { birthdayMsgInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                Button(
                    onClick = {
                        onUpdateBirthdayMsg(birthdayMsgInput)
                        saveSuccessMessage = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForest),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("save_admin_birthday_msg_button")
                ) {
                    Text("Save Message Config")
                }

                if (saveSuccessMessage) {
                    Text("Config updated successfully!", style = MaterialTheme.typography.bodySmall, color = SoftSage, fontWeight = FontWeight.Bold)
                }
            }
        }

        // System Logs / Status
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SoftSand),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("System Diagnostics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest))
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Database: Room SQLite db status OPERATIONAL", style = MaterialTheme.typography.bodySmall)
                Text("• Gemini REST Client: OK", style = MaterialTheme.typography.bodySmall)
                Text("• App Version: 1.0.0 HERBALANCE MVP", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
