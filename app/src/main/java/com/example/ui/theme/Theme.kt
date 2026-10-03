package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonRed,
    onPrimary = Color.White,
    primaryContainer = CrimsonDark,
    onPrimaryContainer = Color.White,
    secondary = AmberGold,
    onSecondary = Color.Black,
    tertiary = CyberCyan,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = CrimsonRed,
    onPrimary = Color.White,
    secondary = AmberGold,
    onSecondary = Color.Black,
    tertiary = CyberCyan,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = Color.White,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFD0D7E2)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to stunning cinematic dark theme for movie streaming
    dynamicColor: Boolean = false, // Keep signature brand palette
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
