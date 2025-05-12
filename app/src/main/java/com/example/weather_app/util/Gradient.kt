package com.example.weather_app.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.weather_app.ui.theme.DarkBlue1
import com.example.weather_app.ui.theme.DarkBlue2
import com.example.weather_app.ui.theme.DarkBlue3
import com.example.weather_app.ui.theme.DeepBlue1
import com.example.weather_app.ui.theme.DeepBlue2
import com.example.weather_app.ui.theme.DeepBlue3

val mainGradientColors = listOf(
    DarkBlue1,
    DarkBlue2,
    DarkBlue3
)
val cardGradientColors = listOf(
    DeepBlue1,
    DeepBlue2,
    DeepBlue1
)
val forecastGradientColors = listOf(
    DarkBlue2,
    DarkBlue3
)

val bottomNavGradientColors = listOf(
    DarkBlue1,DeepBlue2
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