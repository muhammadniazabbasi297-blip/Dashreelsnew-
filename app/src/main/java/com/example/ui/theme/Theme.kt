package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CinematicDarkColorScheme = darkColorScheme(
    primary = ReelRed,
    onPrimary = TextPrimary,
    primaryContainer = ReelCrimson,
    onPrimaryContainer = TextPrimary,
    secondary = VipGold,
    onSecondary = ObsidianDark,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = VipGoldBright,
    tertiary = NeonPurple,
    onTertiary = TextPrimary,
    background = ObsidianDark,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = DarkSurfaceCard
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Streaming reels app default to immersive dark cinema mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CinematicDarkColorScheme,
        typography = Typography,
        content = content
    )
}
