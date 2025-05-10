package com.example.weather_app.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val gradientColors = listOf(
    Color(0xFF00BFFF),
    Color(0xFF1E90FF),
    Color(0xFF4682B4)
)

@Composable
internal fun gradientBackgroundBrush(
    isVerticalGradient: Boolean = true,
    colors: List<Color>
): Brush {
    val endOffset = if(isVerticalGradient){
        Offset(0f,Float.POSITIVE_INFINITY)
    }
    else{
        Offset(Float.POSITIVE_INFINITY, 0f)
    }

    return Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = endOffset
    )
}