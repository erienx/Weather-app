package com.example.weather_app


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    val screens = listOf(Screen.Search, Screen.Weather, Screen.Favourites)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, items = screens)
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Search.route, Modifier.padding(innerPadding)) {
            composable(Screen.Search.route) {
                SearchScreen(navController)
            }
            composable(Screen.Weather.route) {
                WeatherScreen()
            }
            composable(route = "weather/{cityName}", arguments = listOf(navArgument("cityName") { type = NavType.StringType }))
            { backStackEntry ->
                val city = backStackEntry.arguments?.getString("cityName")
                WeatherScreen(city.toString())
            }
            composable(Screen.Favourites.route) {
                FavouritesScreen()
            }
        }
    }
}

@Composable
private fun Content() {
    MainScreen()
}

@Composable
@Preview(showBackground = true)
fun Preview(){
    Content()
}