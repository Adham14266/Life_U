package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryBlue,
    primaryContainer = PrimaryBlueContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = SecondaryGreen,
    onSecondary = OnSecondaryGreen,
    secondaryContainer = SecondaryGreenContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = TertiaryNavy,
    onTertiary = OnTertiaryNavy,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = BackgroundSlate,
    onBackground = OnBackgroundSlate,
    surface = SurfaceSlate,
    onSurface = OnSurfaceSlate,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = OnSurfaceVariantSlate,
    outline = OutlineSlate,
    outlineVariant = OutlineVariantSlate,
    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueContainer,
    onPrimary = OnPrimaryBlue,
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = SecondaryGreenContainer,
    onSecondary = OnSecondaryContainer,
    secondaryContainer = SecondaryGreen,
    onSecondaryContainer = OnSecondaryGreen,
    tertiary = OnTertiaryContainer,
    onTertiary = OnTertiaryNavy,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = OnBackgroundSlate,
    onBackground = BackgroundSlate,
    surface = OnBackgroundSlate,
    onSurface = BackgroundSlate,
    surfaceVariant = OnSurfaceVariantSlate,
    onSurfaceVariant = SurfaceHigh,
    outline = OutlineSlate,
    outlineVariant = OutlineVariantSlate,
    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
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
