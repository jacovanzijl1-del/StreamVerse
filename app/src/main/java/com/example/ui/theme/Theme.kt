package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val StreamColorScheme = darkColorScheme(
    primary = StreamPrimary,
    onPrimary = StreamTextPrimary,
    primaryContainer = StreamPrimaryVariant,
    onPrimaryContainer = StreamTextPrimary,
    secondary = StreamSecondary,
    onSecondary = StreamTextPrimary,
    tertiary = StreamTertiary,
    onTertiary = StreamBackground,
    background = StreamBackground,
    onBackground = StreamTextPrimary,
    surface = StreamSurface,
    onSurface = StreamTextPrimary,
    surfaceVariant = StreamSurfaceVariant,
    onSurfaceVariant = StreamTextSecondary,
    outline = StreamBorder,
    surfaceContainer = StreamCard
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StreamColorScheme,
        typography = Typography,
        content = content
    )
}

