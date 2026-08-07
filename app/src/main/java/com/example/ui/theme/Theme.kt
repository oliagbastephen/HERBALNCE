package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = DeepForest,
    onPrimary = Color.White,
    primaryContainer = SoftSage.copy(alpha = 0.3f),
    onPrimaryContainer = DeepForest,
    secondary = WarmTerracotta,
    onSecondary = Color.White,
    secondaryContainer = SoftSand,
    onSecondaryContainer = DeepCharcoal,
    tertiary = SoftSage,
    onTertiary = DeepForest,
    background = WarmIvory,
    onBackground = DeepCharcoal,
    surface = CardBackground,
    onSurface = DeepCharcoal,
    surfaceVariant = SoftSand,
    onSurfaceVariant = MutedText,
    outline = SoftSage,
    outlineVariant = SoftSand
)

private val DarkColorScheme = darkColorScheme(
    primary = SoftSage,
    onPrimary = DeepForest,
    primaryContainer = DeepForest,
    onPrimaryContainer = WarmIvory,
    secondary = WarmTerracotta,
    onSecondary = Color.White,
    secondaryContainer = DarkSurface,
    onSecondaryContainer = WarmIvory,
    tertiary = SoftSage,
    onTertiary = DarkCanvas,
    background = DarkCanvas,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = SoftSage,
    outline = SoftSage.copy(alpha = 0.5f)
)

@Composable
fun HerbalanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    HerbalanceTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
