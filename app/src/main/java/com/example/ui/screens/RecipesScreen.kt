package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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

@Composable
fun RecipesScreen(
    recipes: List<RecipeEntity>,
    onSelectRecipe: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredRecipes = recipes.filter { recipe ->
        val matchesSearch = recipe.title.contains(searchQuery, ignoreCase = true) ||
                recipe.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || recipe.category.equals(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Nourishing Recipes",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                )
            )
            Text(
                text = "Delicious, nutrient-dense dishes tailored for women's health.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search recipes or ingredients...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recipe_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Breakfast", "Lunch", "Dinner", "Cultural").forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SoftSand,
                            selectedLabelColor = DeepForest
                        )
                    )
                }
            }
        }

        // Recipe Items
        if (filteredRecipes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No recipes match your filter.", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                }
            }
        } else {
            items(filteredRecipes) { recipe ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectRecipe(recipe.id) }
                        .testTag("recipe_card_${recipe.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SuggestionChip(
                                onClick = { },
                                label = { Text(recipe.category, style = MaterialTheme.typography.labelSmall) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = SoftSand)
                            )
                            Text(
                                text = "${recipe.prepTimeMinutes + recipe.cookTimeMinutes} mins total",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = recipe.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepForest)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = recipe.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = DeepCharcoal
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("${recipe.calories} kcal", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text("${recipe.protein}g Protein", style = MaterialTheme.typography.labelMedium, color = WarmTerracotta)
                            Text("${recipe.fiber}g Fiber", style = MaterialTheme.typography.labelMedium, color = SoftSage)
                        }
                    }
                }
            }
        }
    }
}
