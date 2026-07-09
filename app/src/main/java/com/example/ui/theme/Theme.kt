package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryBlue,
    primaryContainer = PrimaryBlueContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = SecondaryGreen,
    onSecondary = OnSecondaryGreen,
    secondaryContainer = SecondaryGreenContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = TertiaryViolet,
    onTertiary = OnTertiaryViolet,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = BackgroundSlate,
    onBackground = OnBackgroundSlate,
    surface = SurfaceLowest,
    onSurface = OnSurfaceSlate,
    surfaceVariant = SurfaceLow,
    onSurfaceVariant = OnSurfaceVariantSlate,
    surfaceTint = PrimaryBlue,
    outline = OutlineSlate,
    outlineVariant = OutlineVariantSlate,
    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    inverseSurface = OnBackgroundSlate,
    inverseOnSurface = BackgroundSlate,
    inversePrimary = PrimaryBlueContainer,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryBlue,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = PrimaryBlueContainer,
    secondary = SecondaryGreen,
    onSecondary = OnSecondaryGreen,
    secondaryContainer = SecondaryGreenDark,
    onSecondaryContainer = SecondaryGreenContainer,
    tertiary = TertiaryViolet,
    onTertiary = OnTertiaryViolet,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = OnBackgroundSlate,
    onBackground = BackgroundSlate,
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    surfaceTint = PrimaryBlue,
    outline = OutlineSlate,
    outlineVariant = Color(0xFF475569),
    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = Color(0xFF450A0A),
    onErrorContainer = Color(0xFFFECACA),
    inverseSurface = BackgroundSlate,
    inverseOnSurface = OnBackgroundSlate,
    inversePrimary = PrimaryBlue,
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
