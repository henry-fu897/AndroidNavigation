package com.example.navigation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.navigation.R

val DisplayFont = FontFamily(Font(R.font.pacifico_regular))
val BodyFont = FontFamily(Font(R.font.special_elite_regular))

private val Base = Typography()

val Typography = Typography(
    // (Display, headline, title) use the script font
    displayLarge = Base.displayLarge.copy(fontFamily = DisplayFont),
    displayMedium = Base.displayMedium.copy(fontFamily = DisplayFont),
    displaySmall = Base.displaySmall.copy(fontFamily = DisplayFont),
    headlineLarge = Base.headlineLarge.copy(fontFamily = DisplayFont),
    headlineMedium = Base.headlineMedium.copy(fontFamily = DisplayFont),
    headlineSmall = Base.headlineSmall.copy(fontFamily = DisplayFont),
    titleLarge = Base.titleLarge.copy(fontFamily = DisplayFont),
    titleMedium = Base.titleMedium.copy(fontFamily = DisplayFont),
    titleSmall = Base.titleSmall.copy(fontFamily = DisplayFont),

    // (Body, label) use the typewriter font
    bodyLarge = Base.bodyLarge.copy(fontFamily = BodyFont),
    bodyMedium = Base.bodyMedium.copy(fontFamily = BodyFont),
    bodySmall = Base.bodySmall.copy(fontFamily = BodyFont),
    labelLarge = Base.labelLarge.copy(fontFamily = BodyFont),
    labelMedium = Base.labelMedium.copy(fontFamily = BodyFont),
    labelSmall = Base.labelSmall.copy(fontFamily = BodyFont),
)