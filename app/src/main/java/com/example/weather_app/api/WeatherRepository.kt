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
    suspend fun getWeather(city: String, key: String): ApiData {
        return api.getCurrentWeather(city, key)
    }
}