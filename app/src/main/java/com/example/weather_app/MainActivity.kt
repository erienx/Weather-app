package com.example.weather_app


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.example.weather_app.util.LocationData
import com.example.weather_app.util.Screen
import com.example.weather_app.util.getLastRefresh
import com.example.weather_app.util.getRefreshInterval
import com.example.weather_app.util.gradientBackgroundBrush
import com.example.weather_app.util.mainGradientColors
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
    val context = LocalContext.current
    val navController = rememberNavController()

    val screens = listOf(Screen.Search, Screen.Weather, Screen.Favourites, Screen.Settings)

    var selectedLocation by remember { mutableStateOf<LocationData?>(null) }

    var refreshTrigger by remember { mutableStateOf(0) }
    var favouritesRefreshTrigger by remember { mutableStateOf(0) }

    val isWideScreen = context.resources.configuration.screenWidthDp >= 1200

    if (isWideScreen) {
        Row(Modifier.fillMaxSize().background(gradientBackgroundBrush(colors = mainGradientColors))) {
            Column(Modifier.weight(1f).padding(8.dp)) {
                SearchScreen(navController = navController, onLocationSelected = { selectedLocation = it }, onFavouriteToggled = { favouritesRefreshTrigger++ })
            }
            Column(Modifier.weight(1f).padding(8.dp)) {
                FavouritesScreen(navController = navController, onLocationSelected = {selectedLocation = it }, key = favouritesRefreshTrigger)
            }
            Column(Modifier.weight(1f).padding(8.dp)) {
                WeatherScreen(lat = selectedLocation?.lat, lon = selectedLocation?.lon, key = refreshTrigger)
            }
            Column(Modifier.weight(1f).padding(8.dp)) {
                SettingsScreen(onUnitChanged = { refreshTrigger++ })
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController, items = screens)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Search.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Search.route) {
                    SearchScreen(navController)
                }
                composable(Screen.Weather.route) {
                    WeatherScreen()
                }

                composable(Screen.Favourites.route) {
                    FavouritesScreen(navController)
                }


                composable(
                    route = "weather/{lat}/{lon}",
                    arguments = listOf(navArgument("lat") { type = NavType.StringType }, navArgument("lon") { type = NavType.StringType })
                ) { backStackEntry ->
                    val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
                    val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()
                    WeatherScreen(lat = lat, lon = lon)
                }

                composable(Screen.Settings.route) { SettingsScreen() }
            }
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
fun Preview() {
    Content()
}

@Composable
fun AutoRefreshData(context: Context, viewModel: WeatherView) {
    LaunchedEffect(Unit) {
        while (true) {
            val interval = getRefreshInterval(context)
            val last = getLastRefresh(context)
            val currentTime = System.currentTimeMillis()

            if (currentTime - last > interval * 60000) {
                viewModel.refreshFavoritesDataAndDisplayToast(context)
                Log.d("WeatherApp", "Auto-refreshed favorite cities data")
            }

            delay(1000 * 20)
        }
    }
}