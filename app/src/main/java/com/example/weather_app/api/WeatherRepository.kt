package com.example.weather_app.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {
    private val api: WeatherApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(WeatherApi::class.java)
    }
    suspend fun getWeather(city: String, key: String): ApiDataCurrent {
        return api.getCurrentWeather(city, key)
    }
    suspend fun getForecast(city: String, key: String): ApiDataForecast {
        return api.getForecast(city, key)
    }
    suspend fun getCitySuggestions(query: String, key: String): List<ApiDataGeocoding> {
        return if (query.length >= 2) {
            try {
                api.getCitiesByName(query, 5, key)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    suspend fun getCurrentByCoords(lat: Double, lon: Double, key: String): ApiDataCurrent{
        return api.getCurrentWeatherByCoords(lat,lon, key)
    }
    suspend fun getForecastByCoords(lat: Double, lon: Double, key: String): ApiDataForecast{
        return api.getForecastByCoords(lat,lon, key)
    }

}