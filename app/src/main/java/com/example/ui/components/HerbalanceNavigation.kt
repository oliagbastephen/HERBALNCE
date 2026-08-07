package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HerbalanceTopBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onOpenDrawer: () -> Unit,
    hasNotifications: Boolean = true
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNavigate(Screen.Dashboard) }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(DeepForest),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "H",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = WarmIvory,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Text(
                    text = "HERBALANCE",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = DeepForest
                    )
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier
                    .testTag("menu_button")
                    .minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Navigation Menu",
                    tint = DeepForest
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onNavigate(Screen.VoiceConversation) },
                modifier = Modifier
                    .testTag("voice_assistant_button")
                    .minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Mic,
                    contentDescription = "Voice Assistant",
                    tint = DeepForest
                )
            }
            IconButton(
                onClick = { onNavigate(Screen.DailyCheckIn) },
                modifier = Modifier
                    .testTag("checkin_button")
                    .minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Mood,
                    contentDescription = "Daily Check-In",
                    tint = DeepForest
                )
            }
            IconButton(
                onClick = { onNavigate(Screen.Settings) },
                modifier = Modifier
                    .testTag("settings_button")
                    .minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = DeepForest
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WarmIvory
        )
    )
}

@Composable
fun HerbalanceBottomNav(
    currentRoute: String,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = CardBackground,
        contentColor = DeepForest,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
    ) {
        val items = listOf(
            Triple(Screen.Dashboard, Icons.Filled.Home, Icons.Outlined.Home),
            Triple(Screen.Meals, Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu),
            Triple(Screen.FoodLog, Icons.Filled.AddCircle, Icons.Outlined.AddCircleOutline),
            Triple(Screen.Progress, Icons.Filled.BarChart, Icons.Outlined.BarChart),
            Triple(Screen.Profile, Icons.Filled.Person, Icons.Outlined.Person)
        )

        items.forEach { (screen, filledIcon, outlinedIcon) ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DeepForest,
                    selectedTextColor = DeepForest,
                    indicatorColor = SoftSand,
                    unselectedIconColor = MutedText,
                    unselectedTextColor = MutedText
                ),
                modifier = Modifier.testTag("nav_item_${screen.route}")
            )
        }
    }
}

@Composable
fun HerbalanceDrawerContent(
    currentRoute: String,
    onNavigate: (Screen) -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = WarmIvory,
        drawerContentColor = DeepCharcoal,
        modifier = Modifier.width(300.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DeepForest),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "H",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = WarmIvory,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "HERBALANCE",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = DeepForest
                    )
                )
                Text(
                    text = "Eat Better. Feel Better.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedText
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SoftSand)

        val navEntries = listOf(
            Screen.Dashboard to Icons.Outlined.Home,
            Screen.VoiceConversation to Icons.Outlined.Mic,
            Screen.Meals to Icons.Outlined.RestaurantMenu,
            Screen.FoodLog to Icons.Outlined.MenuBook,
            Screen.Water to Icons.Outlined.WaterDrop,
            Screen.Weight to Icons.Outlined.MonitorWeight,
            Screen.Progress to Icons.Outlined.ShowChart,
            Screen.Recipes to Icons.Outlined.MenuBook,
            Screen.ShoppingList to Icons.Outlined.ShoppingCart,
            Screen.Learn to Icons.Outlined.School,
            Screen.DailyCheckIn to Icons.Outlined.Mood,
            Screen.Profile to Icons.Outlined.Person,
            Screen.Settings to Icons.Outlined.Settings,
            Screen.Admin to Icons.Outlined.AdminPanelSettings
        )

        Column(modifier = Modifier.fillMaxHeight()) {
            navEntries.forEach { (screen, icon) ->
                val isSelected = currentRoute == screen.route
                NavigationDrawerItem(
                    label = { Text(screen.title) },
                    selected = isSelected,
                    onClick = {
                        onNavigate(screen)
                        onCloseDrawer()
                    },
                    icon = { Icon(icon, contentDescription = screen.title) },
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .testTag("drawer_item_${screen.route}"),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = SoftSand,
                        selectedIconColor = DeepForest,
                        selectedTextColor = DeepForest,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = DeepCharcoal
                    )
                )
            }
        }
    }
}
