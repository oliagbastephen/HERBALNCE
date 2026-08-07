package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

data class LearnArticle(
    val title: String,
    val category: String,
    val readTime: String,
    val summary: String,
    val content: String
)

@Composable
fun LearnScreen() {
    var selectedArticle by remember { mutableStateOf<LearnArticle?>(null) }

    val articles = listOf(
        LearnArticle(
            title = "Protein & Lean Muscle Support for Women in Their 30s",
            category = "Macronutrients",
            readTime = "3 min read",
            summary = "Why adequate protein intake maintains metabolic efficiency, bone density, and steady appetite.",
            content = "Starting in our 30s, natural lean muscle mass gradually decreases unless supported by adequate dietary protein and strength movement. Consuming 20-30 grams of protein per meal supports neurotransmitter production, repairs tissue, and keeps hunger hormones balanced throughout your day."
        ),
        LearnArticle(
            title = "The Power of Fiber: Digestion & Hormone Balance",
            category = "Micronutrients",
            readTime = "4 min read",
            summary = "How soluble and insoluble fiber help regulate blood sugar and support daily detoxification.",
            content = "Dietary fiber acts as a natural prebiotic, feeding beneficial gut microbes that influence immune resilience and mood regulation. Aiming for 25-30 grams daily from beans, sweet potatoes, greens, and berries prevents glucose spikes and stabilizes energy."
        ),
        LearnArticle(
            title = "Essential Minerals: Iron, Magnesium & Calcium",
            category = "Women's Health",
            readTime = "3 min read",
            summary = "Key micronutrients every woman should prioritize for deep sleep and cellular vitality.",
            content = "Magnesium supports over 300 enzyme reactions and promotes muscle relaxation, while iron maintains oxygen transport to tissues. Pairing iron-rich plant foods like legumes or dark leafy greens with Vitamin C (such as citrus or red pepper) increases absorption dramatically."
        ),
        LearnArticle(
            title = "Gentle Hydration: Beyond Just Drinking Water",
            category = "Lifestyle",
            readTime = "2 min read",
            summary = "How electrolyte balance and water-rich foods keep your skin glowing and mind sharp.",
            content = "True cellular hydration involves both clean water and natural minerals like potassium and sodium. Incorporating cucumbers, citrus slices, coconut water, and herbal teas ensures optimal fluid distribution without constant flushing."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Women's Health Education",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                )
            )
            Text(
                text = "Evidence-based, non-clinical nutrition knowledge for lifelong vitality.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )
        }

        items(articles) { article ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedArticle = article }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(article.category) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = SoftSand)
                        )
                        Text(article.readTime, style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(article.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest))

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(article.summary, style = MaterialTheme.typography.bodySmall, color = DeepCharcoal)
                }
            }
        }
    }

    selectedArticle?.let { article ->
        AlertDialog(
            onDismissRequest = { selectedArticle = null },
            title = { Text(article.title, style = MaterialTheme.typography.titleLarge) },
            text = { Text(article.content, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(
                    onClick = { selectedArticle = null },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForest)
                ) {
                    Text("Close")
                }
            },
            containerColor = WarmIvory
        )
    }
}
