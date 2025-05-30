package com.example.whosdaresample.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFFCC00FF),
    onPrimary = Color.Black,
    secondary = Color.Cyan,
    tertiary = Pink40,
    background = Color(0xFFEFE7F6),
    onBackground = Color.Black,
    surface = Color(0xFFE8DAF6)
)

// Dunkles Theme = dein aktuelles Design
private val DarkColors = darkColorScheme(
    primary = Color.Cyan,
    onPrimary = Color.Black,
    secondary = Color.Magenta,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF232222)
)

@Composable
fun WhosdaresampleTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
