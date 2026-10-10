package com.example.navigation.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = CutCornerShape(16.dp),
    small = CutCornerShape(16.dp),
    /*
    medium = RectangleShape,
    medium = RoundedCornerShape(topStart = 28.dp, bottomEnd = 28.dp, topEnd = 0.dp, bottomStart = 0.dp),
     */
    medium = CutCornerShape(20.dp),
    large = CutCornerShape(28.dp),
    extraLarge = CutCornerShape(36.dp)
)
