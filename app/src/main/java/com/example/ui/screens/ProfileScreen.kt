package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    profile: UserProfileEntity?,
    onNavigate: (Screen) -> Unit,
    onEditProfile: () -> Unit
) {
    val user = profile ?: UserProfileEntity()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(DeepForest),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(1).uppercase(),
                        style = MaterialTheme.typography.displayMedium.copy(color = WarmIvory, fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontFamily = DisplayFontFamily, fontWeight = FontWeight.Bold, color = DeepForest)
                )

                Text(
                    text = "${user.age} yrs | ${user.heightCm.toInt()} cm | ${user.weightKg} kg",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onEditProfile,
                    colors = ButtonDefaults.buttonColors(containerColor = WarmTerracotta),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("edit_profile_button")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Preferences & Onboarding")
                }
            }
        }

        // Birthday Gift Experience Banner
        if (user.isGiftMode) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSand),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Birthday Gift Mode Active ❤️",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = user.birthdayMsg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeepCharcoal
                    )
                }
            }
        }

        // Nutritional Target Summary
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Nutritional Targets", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest))
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ProfileStat("Calorie Target", "${user.calorieTarget} kcal")
                    ProfileStat("Water Goal", "${user.waterTargetMl} ml")
                    ProfileStat("Protein Target", "${user.proteinTargetGrams} g")
                }
            }
        }

        // Dietary & Allergies
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Preferences & Safety Constraints", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest))
                Spacer(modifier = Modifier.height(4.dp))

                Text("• Goals: ${user.goals}", style = MaterialTheme.typography.bodyMedium)
                Text("• Cuisines: ${user.cuisinePreferences}", style = MaterialTheme.typography.bodyMedium)
                Text("• Restrictions: ${user.dietaryRestrictions}", style = MaterialTheme.typography.bodyMedium)
                Text("• Allergies (Strict): ${user.allergies}", style = MaterialTheme.typography.bodyMedium, color = WarmTerracotta, fontWeight = FontWeight.Bold)
            }
        }

        OutlinedButton(
            onClick = { onNavigate(Screen.Settings) },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("App Settings & Privacy", color = DeepForest)
        }
    }
}

@Composable
fun ProfileStat(label: String, value: String) {
    Column {
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}
