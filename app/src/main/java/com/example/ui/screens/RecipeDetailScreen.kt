package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.RecipeEntity
import com.example.ui.theme.*
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipe: RecipeEntity?,
    onBack: () -> Unit,
    onAddIngredientsToShoppingList: (List<String>) -> Unit
) {
    if (recipe == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Recipe not found.", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val ingredients = remember(recipe.ingredientsJson) {
        try {
            Json.decodeFromString<List<String>>(recipe.ingredientsJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    val instructions = remember(recipe.instructionsJson) {
        try {
            Json.decodeFromString<List<String>>(recipe.instructionsJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmIvory)
            )
        },
        containerColor = WarmIvory
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = DeepForest
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(recipe.description, style = MaterialTheme.typography.bodyMedium, color = MutedText)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailChip("Prep", "${recipe.prepTimeMinutes} mins")
                        DetailChip("Cook", "${recipe.cookTimeMinutes} mins")
                        DetailChip("Servings", "${recipe.servings}")
                        DetailChip("Calories", "${recipe.calories} kcal")
                    }
                }
            }

            // Ingredients Section
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSand),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ingredients",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = DeepForest
                            )
                        )

                        TextButton(
                            onClick = { onAddIngredientsToShoppingList(ingredients) },
                            modifier = Modifier.testTag("add_to_shopping_list_button")
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Shopping List", color = WarmTerracotta)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ingredients.forEach { item ->
                        Text("• $item", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }

            // Step-by-Step Instructions
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Instructions",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = DeepForest
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    instructions.forEachIndexed { idx, step ->
                        Row(modifier = Modifier.padding(vertical = 6.dp)) {
                            Text(
                                text = "${idx + 1}.",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WarmTerracotta)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(step, style = MaterialTheme.typography.bodyMedium, color = DeepCharcoal)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}
