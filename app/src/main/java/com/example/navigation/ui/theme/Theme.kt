package com.example.navigation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

private val LightHighContrastColorScheme = lightColorScheme(
    primary = HighContrastLightPrimary,
    onPrimary = HighContrastLightOnPrimary,
    primaryContainer = HighContrastLightPrimaryContainer,
    onPrimaryContainer = HighContrastLightOnPrimaryContainer,
    secondary = HighContrastLightSecondary,
    onSecondary = HighContrastLightOnSecondary,
    secondaryContainer = HighContrastLightSecondaryContainer,
    onSecondaryContainer = HighContrastLightOnSecondaryContainer,
    tertiary = HighContrastLightTertiary,
    onTertiary = HighContrastLightOnTertiary,
    tertiaryContainer = HighContrastLightTertiaryContainer,
    onTertiaryContainer = HighContrastLightOnTertiaryContainer,
    background = HighContrastLightBackground,
    onBackground = HighContrastLightOnBackground,
    surface = HighContrastLightSurface,
    onSurface = HighContrastLightOnSurface,
    surfaceVariant = HighContrastLightSurfaceVariant,
    onSurfaceVariant = HighContrastLightOnSurfaceVariant,
    outline = HighContrastLightOutline,
    error = HighContrastLightError,
    onError = HighContrastLightOnError,
    errorContainer = HighContrastLightErrorContainer,
    onErrorContainer = HighContrastLightOnErrorContainer,
    surfaceDim = HighContrastLightSurfaceDim,
    surfaceBright = HighContrastLightSurfaceBright,
    surfaceContainerLowest = HighContrastLightSurfaceContainerLowest,
    surfaceContainerLow = HighContrastLightSurfaceContainerLow,
    surfaceContainer = HighContrastLightSurfaceContainer,
    surfaceContainerHigh = HighContrastLightSurfaceContainerHigh,
    surfaceContainerHighest = HighContrastLightSurfaceContainerHighest
)

private val DarkHighContrastColorScheme = darkColorScheme(
    primary = HighContrastDarkPrimary,
    onPrimary = HighContrastDarkOnPrimary,
    primaryContainer = HighContrastDarkPrimaryContainer,
    onPrimaryContainer = HighContrastDarkOnPrimaryContainer,
    secondary = HighContrastDarkSecondary,
    onSecondary = HighContrastDarkOnSecondary,
    secondaryContainer = HighContrastDarkSecondaryContainer,
    onSecondaryContainer = HighContrastDarkOnSecondaryContainer,
    tertiary = HighContrastDarkTertiary,
    onTertiary = HighContrastDarkOnTertiary,
    tertiaryContainer = HighContrastDarkTertiaryContainer,
    onTertiaryContainer = HighContrastDarkOnTertiaryContainer,
    background = HighContrastDarkBackground,
    onBackground = HighContrastDarkOnBackground,
    surface = HighContrastDarkSurface,
    onSurface = HighContrastDarkOnSurface,
    surfaceVariant = HighContrastDarkSurfaceVariant,
    onSurfaceVariant = HighContrastDarkOnSurfaceVariant,
    outline = HighContrastDarkOutline,
    error = HighContrastDarkError,
    onError = HighContrastDarkOnError,
    errorContainer = HighContrastDarkErrorContainer,
    onErrorContainer = HighContrastDarkOnErrorContainer,
    surfaceDim = HighContrastDarkSurfaceDim,
    surfaceBright = HighContrastDarkSurfaceBright,
    surfaceContainerLowest = HighContrastDarkSurfaceContainerLowest,
    surfaceContainerLow = HighContrastDarkSurfaceContainerLow,
    surfaceContainer = HighContrastDarkSurfaceContainer,
    surfaceContainerHigh = HighContrastDarkSurfaceContainerHigh,
    surfaceContainerHighest = HighContrastDarkSurfaceContainerHighest
)

@Composable
fun NavigationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast && darkTheme -> DarkHighContrastColorScheme
        highContrast -> LightHighContrastColorScheme

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val semanticColors = when {
        highContrast && darkTheme -> HighContrastDarkSemanticColors
        highContrast -> HighContrastLightSemanticColors
        darkTheme -> DarkSemanticColors
        else -> LightSemanticColors
    }

    CompositionLocalProvider(LocalSemanticColors provides semanticColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}