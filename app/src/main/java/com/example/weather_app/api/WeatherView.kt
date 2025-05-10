package com.example.weather_app.api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherView : ViewModel() {
    private val repository = WeatherRepository()

    var fetchStatus by mutableStateOf<WeatherUIState>(WeatherUIState.Loading)
        private set

    fun fetchWeather(city: String, apiKey: String) {
        viewModelScope.launch {
            fetchStatus = WeatherUIState.Loading
            try {
                val data = repository.getWeather(city, apiKey)
                fetchStatus = WeatherUIState.Success(data)
            } catch (e: Exception) {
                fetchStatus = WeatherUIState.Error(e.message ?: "Error")
            }
        }
    }
}


sealed class WeatherUIState {
    object Loading : WeatherUIState()
    data class Success(val data: ApiData) : WeatherUIState()
    data class Error(val message: String) : WeatherUIState()
}