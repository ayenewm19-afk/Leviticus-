package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    secondary = BurgundyLight,
    tertiary = GoldAccentLight,
    background = ParchmentDark,
    surface = SurfaceDark,
    onPrimary = BurgundyDark,
    onSecondary = OnBackgroundDark,
    onTertiary = BurgundyDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = BurgundyPrimary,
    secondary = GoldAccent,
    tertiary = BurgundyLight,
    background = ParchmentLight,
    surface = SurfaceLight,
    onPrimary = OnPrimaryLight,
    onSecondary = BurgundyDark,
    onTertiary = OnPrimaryLight,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
