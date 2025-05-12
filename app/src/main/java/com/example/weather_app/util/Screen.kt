package com.example.weather_app.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Weather : Screen("weather", "Weather", Icons.Default.Info)
    object Favourites : Screen("favourites", "Favourites", Icons.Default.Favorite)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}