package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TwilightColorScheme = darkColorScheme(
    primary = WarmAmberGold,
    secondary = DarkGoldAccent,
    tertiary = SoftSlateBlue,
    background = DeepIndigoBackground,
    surface = NavyCharcoal,
    onPrimary = DeepIndigoBackground,
    onSecondary = DeepIndigoBackground,
    onTertiary = CreamWhite,
    onBackground = CreamWhite,
    onSurface = CreamWhite,
    outline = SoftSlateBlue
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TwilightColorScheme,
        typography = Typography,
        content = content
    )
}
