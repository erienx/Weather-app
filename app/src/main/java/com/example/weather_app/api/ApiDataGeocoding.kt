package com.example.weather_app.api

data class ApiDataGeocoding(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null
)