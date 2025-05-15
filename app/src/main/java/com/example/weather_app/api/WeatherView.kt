package com.example.weather_app.api

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.util.API_KEY
import com.example.weather_app.util.WeatherData
import com.example.weather_app.util.getFavourites
import com.example.weather_app.util.saveFavourites
import com.example.weather_app.util.saveLastRefresh
import com.example.weather_app.util.toast
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.debounce


class WeatherView : ViewModel() {
    private val repository = WeatherRepository()

    var fetchStatus by mutableStateOf<WeatherUIState>(WeatherUIState.Loading)
        private set

    val citySuggestions = MutableStateFlow<List<ApiDataGeocoding>>(emptyList())

    private val searchQuery = MutableStateFlow("")

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(500)
                .collect { query ->
                    if (query.length >= 2) {
                        fetchCitySuggestions(query)
                    } else {
                        citySuggestions.value = emptyList()
                    }
                }
        }
    }
    public fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    private fun fetchCitySuggestions(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                val suggestions = repository.getCitySuggestions(query, API_KEY)
                citySuggestions.value = suggestions
            } catch (e: Exception) {
                citySuggestions.value = emptyList()
            }
        }
    }

    fun fetchWeatherData(city: String, apiKey: String) {
        viewModelScope.launch {
            fetchStatus = WeatherUIState.Loading
            try {
                val currentData = repository.getWeather(city, apiKey)
                val forecastData = repository.getForecast(city, apiKey)
                fetchStatus = WeatherUIState.Success(currentData, forecastData)
            } catch (e: Exception) {
                fetchStatus = WeatherUIState.Error(e.message ?: "Error")
            }
        }
    }

    fun refreshFavoritesDataAndDisplayToast(context: Context) {
        viewModelScope.launch {
            var fetchFailed = false
            val favourites = getFavourites(context)

            val updatedFavorites = mutableListOf<WeatherData>()
            for (weatherData in favourites) {
                val city = weatherData.city
                try {
                    val currentData = repository.getWeather(city, API_KEY)
                    val forecastData = repository.getForecast(city, API_KEY)

                    updatedFavorites.add(WeatherData(city = city, current = currentData, forecast = forecastData))

                    Log.e("aa", "updated data")
                } catch (e: Exception) {
                    updatedFavorites.add(WeatherData(city = city, current = weatherData.current, forecast = weatherData.forecast))
                    fetchFailed = true
                    Log.e("aa", "kept old data")
                }
            }
            saveFavourites(context, updatedFavorites)
            saveLastRefresh(context, timestamp = System.currentTimeMillis())
            if (fetchFailed)
                context.toast("One or more locations couldn't be refreshed")
            else
                context.toast("Refreshed data")
            Log.e("aa", "refreshed")
        }
    }
}


sealed class WeatherUIState {
    object Loading : WeatherUIState()
    data class Success(val currentData: ApiDataCurrent, val forecastData: ApiDataForecast? = null) : WeatherUIState()
    data class Error(val message: String) : WeatherUIState()
}