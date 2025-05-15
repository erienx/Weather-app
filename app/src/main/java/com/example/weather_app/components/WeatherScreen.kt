package com.example.weather_app.components


import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather_app.api.WeatherUIState
import com.example.weather_app.api.WeatherView
import com.example.weather_app.util.API_KEY
import com.example.weather_app.util.LocationData
import com.example.weather_app.util.getFavourites
import com.example.weather_app.util.getLastViewedData
import com.example.weather_app.util.gradientBackgroundBrush
import com.example.weather_app.util.mainGradientColors
import com.example.weather_app.util.saveLastViewedData

@Composable
fun WeatherScreen(lat: Double? = null, lon: Double? = null, key: Int = 0) {
    val apiKey = API_KEY
    val context = LocalContext.current
    var locationToFetch: LocationData

    val isOfflineLaunch = lat == null || lon == null

    val lastViewed = getLastViewedData(context)

    if (lat == null || lon == null) {
        if (lastViewed != null) {
            locationToFetch = lastViewed.location
        } else {
            locationToFetch = LocationData(city = "lodz", lat = 23.3, lon = 32.3)
            Log.e("aa", "last viewed is null, falling back to default")
        }
    } else {
        locationToFetch = LocationData(lat = lat, lon = lon, city = "")
    }



    val viewModel: WeatherView = viewModel()
    val weatherState = viewModel.fetchStatus

        LaunchedEffect(lat, lon, key) {
            viewModel.fetchWeatherDataByCoords(locationToFetch.lat, locationToFetch.lon, apiKey)
    }
    Box(modifier = Modifier.fillMaxSize().background(brush = gradientBackgroundBrush(colors = mainGradientColors))) {
        when (weatherState) {
            is WeatherUIState.Loading -> {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.Center))
            }

            is WeatherUIState.Error -> {
                var offlineData = lastViewed
//                Log.e("aa", "${offlineData?.location?.lat}, ${lat};  ${offlineData?.location?.lon} ${lon}")
                if ((lat!=null && offlineData?.location?.lat != lat) || (lon!=null && offlineData?.location?.lon != lon)) {
                    offlineData = getFavourites(context).firstOrNull {
                        it.location.lat == lat && it.location.lon == lon
                    }
                }
                if (offlineData != null && offlineData.current != null) {
                    LaunchedEffect(weatherState) {
                        saveLastViewedData(context, offlineData.location, offlineData.current, offlineData.forecast)
                    }

                    Column(modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("Offline mode, data may be inaccurate", modifier = Modifier.padding(16.dp), fontSize = 18.sp, color = Color.White, textAlign = TextAlign.Center)
                        WeatherCard(offlineData.current)
                        if (offlineData.forecast != null) {
                            ForecastList(offlineData.forecast)
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("Weather data fetch failed", color = Color.White, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Check your internet connection", color = Color.White, fontSize = 16.sp)
                    }
                }
            }

            is WeatherUIState.Success -> {
                LaunchedEffect(weatherState) {
                    saveLastViewedData(context, locationToFetch, weatherState.currentData, weatherState.forecastData)
                }
                Log.e("aa", "${locationToFetch.lat} ${locationToFetch.lon}")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                    WeatherCard(weatherState.currentData)
                    if (weatherState.forecastData != null) {
                        ForecastList(weatherState.forecastData)
                    }
                }
            }
        }
    }
}




