package com.example.weather_app


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weather_app.api.WeatherView
import com.example.weather_app.components.BottomNavigationBar
import com.example.weather_app.components.FavouritesScreen
import com.example.weather_app.components.SearchScreen
import com.example.weather_app.components.SettingsScreen
import com.example.weather_app.components.WeatherScreen
import com.example.weather_app.util.Screen
import com.example.weather_app.util.getLastRefresh
import com.example.weather_app.util.getRefreshInterval
import kotlinx.coroutines.delay


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
    val screens = listOf(Screen.Search, Screen.Weather, Screen.Favourites, Screen.Settings)

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
                FavouritesScreen(navController)
            }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}

@Composable
private fun Content() {
    val context = LocalContext.current
    val viewModel: WeatherView = viewModel()

    AutoRefreshData(context, viewModel)
    MainScreen()
}

@Composable
@Preview(showBackground = true)
fun Preview(){
    Content()
}
@Composable
fun AutoRefreshData(context: Context, viewModel: WeatherView) {
    LaunchedEffect(Unit) {
        while (true) {
            val interval = getRefreshInterval(context)
            val last = getLastRefresh(context)
            val currentTime = System.currentTimeMillis()

            if (currentTime - last > interval *  60000) {
                viewModel.refreshFavoritesDataAndDisplayToast(context)
                Log.e("aa", "auto refreshed data", )
            }

            delay(1000 * 20)
        }
    }
}
