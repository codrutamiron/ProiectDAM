package com.example.myweatherapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyWeatherAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = dynamicColorScheme(),
        typography = Typography,
        content = content
    )
}

// Optionally, define static color schemes:
private val LightColors = lightColorScheme(
    primary = Color(0xFF6200EA),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC6),
    // Add other colors as needed
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    secondary = Color(0xFF03DAC6),
    // Add other colors as needed
)

@Composable
private fun dynamicColorScheme(): ColorScheme {
    return if (isSystemInDarkTheme()) DarkColors else LightColors
}
