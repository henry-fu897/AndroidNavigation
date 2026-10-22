package com.example.navigation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class SemanticColors(
    val correct: Color,
    val onCorrect: Color,
    val correctContainer: Color,
    val onCorrectContainer: Color
)

val LightSemanticColors = SemanticColors(
    correct = Color(0xFF2E7D32),
    onCorrect = Color(0xFFFFFFFF),
    correctContainer = Color(0xFFC8E6C9),
    onCorrectContainer = Color(0xFF0A3D12)
)

val DarkSemanticColors = SemanticColors(
    correct = Color(0xFF81C784),
    onCorrect = Color(0xFF0B3D14),
    correctContainer = Color(0xFF1B5E20),
    onCorrectContainer = Color(0xFFC8E6C9)
)

val HighContrastLightSemanticColors = SemanticColors(
    correct = Color(0xFF0B4A12),
    onCorrect = Color(0xFFFFFFFF),
    correctContainer = Color(0xFF9CF2A0),
    onCorrectContainer = Color(0xFF002105)
)

val HighContrastDarkSemanticColors = SemanticColors(
    correct = Color(0xFF9CF2A0),
    onCorrect = Color(0xFF002105),
    correctContainer = Color(0xFF0B4A12),
    onCorrectContainer = Color(0xFF9CF2A0)
)

val LocalSemanticColors = staticCompositionLocalOf<SemanticColors> {
    error("No SemanticColors provided. Wrap your UI in NavigationTheme.")
}

val MaterialTheme.semanticColors: SemanticColors
    @Composable
    @ReadOnlyComposable
    get() = LocalSemanticColors.current