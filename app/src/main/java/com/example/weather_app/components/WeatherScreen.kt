package com.example.weather_app.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather_app.api.ApiDataCurrent
import com.example.weather_app.api.WeatherUIState
import com.example.weather_app.api.WeatherView
import com.example.weather_app.util.gradientBackgroundBrush
import com.example.weather_app.util.mainGradientColors
@Composable
fun WeatherScreen(city: String = "lodz") {
    val apiKey = "0a7cf1e7fd79dacb4e026a76d2f062ff"

    val viewModel: WeatherView = viewModel()
    val weatherState = viewModel.fetchStatus

    LaunchedEffect(key1 = city) {
        viewModel.fetchWeatherData(city, apiKey)
    }

    Box(modifier = Modifier.fillMaxSize().background(brush = gradientBackgroundBrush(colors = mainGradientColors))){
        when (weatherState){
            is WeatherUIState.Loading -> {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.Center))
            }
            is WeatherUIState.Error -> {
                Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                    Text("Weather data fetch failed", color = Color.White, fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(weatherState.message, color=Color.White, fontSize = 16.sp)
                }
            }
            is WeatherUIState.Success -> {
                Column {
                    WeatherInfoContent(weatherState.currentData)
                    if (weatherState.forecastData != null) {
                        ForecastList(weatherState.forecastData)
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherInfoContent(data: ApiDataCurrent){
    WeatherCard(data)
}



