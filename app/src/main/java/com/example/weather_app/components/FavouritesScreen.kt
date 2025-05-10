package com.example.weather_app.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.weather_app.util.gradientBackgroundBrush
import com.example.weather_app.util.mainGradientColors

@Composable
fun FavouritesScreen() {
    Box(modifier = Modifier.fillMaxSize().background(brush = gradientBackgroundBrush(colors = mainGradientColors)), contentAlignment = Alignment.Center) {
        Text("Favourites Screen")
    }
}