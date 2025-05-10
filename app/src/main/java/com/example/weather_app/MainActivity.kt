package com.example.weather_app


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weather_app.components.BottomNavigationBar
import com.example.weather_app.components.FavouritesScreen
import com.example.weather_app.components.SearchScreen
import com.example.weather_app.components.WeatherScreen
import com.example.weather_app.util.Screen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(Screen.Search, Screen.Weather, Screen.Favourites)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, items = items)
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Search.route, Modifier.padding(innerPadding)) {
            composable(Screen.Search.route) { SearchScreen() }
            composable(Screen.Weather.route) { WeatherScreen() }
            composable(Screen.Favourites.route) { FavouritesScreen() }
        }
    }
}

@Composable
private fun Content() {
    MainScreen()
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    Content()
}
