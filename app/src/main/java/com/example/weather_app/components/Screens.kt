package com.example.weather_app.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.weather_app.util.gradientBackgroundBrush
import com.example.weather_app.util.mainGradientColors

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Weather : Screen("weather", "Weather", Icons.Default.Info)
    object Favourites : Screen("favourites", "Favourites", Icons.Default.Favorite)
}

@Composable
fun SearchScreen() {
    Box(modifier = Modifier.fillMaxSize().background(brush = gradientBackgroundBrush(colors = mainGradientColors)), contentAlignment = Alignment.Center) {
        Text("Search Screen")
    }
}

@Composable
fun WeatherScreen() {
    Box(modifier = Modifier.fillMaxSize().background(brush = gradientBackgroundBrush(colors = mainGradientColors)), contentAlignment = Alignment.Center) {
        Text("Weather Info Screen")
    }
}

@Composable
fun FavouritesScreen() {
    Box(modifier = Modifier.fillMaxSize().background(brush = gradientBackgroundBrush(colors = mainGradientColors)), contentAlignment = Alignment.Center) {
        Text("Favourites Screen")
    }
}
