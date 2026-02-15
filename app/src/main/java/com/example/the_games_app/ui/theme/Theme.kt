package com.example.the_games_app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// CHAOS TAP Color Palette
private val ChaosDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00FFA3),        // Electric Green
    secondary = Color(0xFF1E1E1E),      // Dark Gray
    tertiary = Color(0xFFFF3333),       // Red
    background = Color(0xFF121212),     // Pure Black
    surface = Color(0xFF1E1E1E),        // Surface Gray
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color(0xFFFF3333)
)

@Composable
fun ChaosTapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ChaosDarkColorScheme,
        content = content
    )
}