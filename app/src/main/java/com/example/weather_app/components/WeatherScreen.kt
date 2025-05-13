package com.example.weather_app.components


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
import com.example.weather_app.util.getFavourites
import com.example.weather_app.util.getLastViewedData
import com.example.weather_app.util.gradientBackgroundBrush
import com.example.weather_app.util.mainGradientColors
import com.example.weather_app.util.saveLastViewedData

@Composable
fun WeatherScreen(city: String? = null) {
    val apiKey = API_KEY
    val context = LocalContext.current
    var cityToFetch = city

    if (cityToFetch == null){
        cityToFetch = getLastViewedData(context)?.city ?: "lodz"
    }

    val viewModel: WeatherView = viewModel()
    val weatherState = viewModel.fetchStatus

    LaunchedEffect(key1 = cityToFetch) {
        viewModel.fetchWeatherData(cityToFetch, apiKey)
    }

    Box(modifier = Modifier.fillMaxSize().background(brush = gradientBackgroundBrush(colors = mainGradientColors))){
        when (weatherState){
            is WeatherUIState.Loading -> {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.Center))
            }
            is WeatherUIState.Error -> {
                var offlineData = getLastViewedData(context)
                if (!offlineData?.city.equals(cityToFetch)){
                    offlineData = getFavourites(context).firstOrNull { it.city.equals(cityToFetch, ignoreCase = true) }
                }

                if (offlineData != null && offlineData.current != null) {
                    LaunchedEffect(weatherState) {
                        saveLastViewedData(context, cityToFetch, offlineData.current, offlineData.forecast)
                    }
                    Column (modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                        Text("Offline mode, data may be inaccurate", modifier = Modifier.padding(16.dp),  fontSize = 18.sp,color = Color.White, textAlign = TextAlign.Center)
                        WeatherCard(offlineData.current)
                        if (offlineData.forecast != null) {
                            ForecastList(offlineData.forecast)
                        }
                    }
                } else {
                    Column (modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                        Text("Weather data fetch failed", color = Color.White, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("check your internet connection", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
            is WeatherUIState.Success -> {
                LaunchedEffect(weatherState) {
                    saveLastViewedData(context, cityToFetch, weatherState.currentData, weatherState.forecastData)
                }
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally){
                    WeatherCard(weatherState.currentData)
                    if (weatherState.forecastData != null) {
                        ForecastList(weatherState.forecastData)
                    }
                }
            }
        }
    }
}




