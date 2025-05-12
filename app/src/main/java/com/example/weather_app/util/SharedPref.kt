package com.example.weather_app.util

import android.content.Context
import com.example.weather_app.api.ApiDataCurrent
import com.example.weather_app.api.ApiDataForecast
import com.example.weather_app.api.WeatherRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

public const val API_KEY = "0a7cf1e7fd79dacb4e026a76d2f062ff"

private const val PREF_NAME = "weather_prefs"

private const val SEARCH_HISTORY = "search_history"

fun getSearchHistory(context: Context): List<String> {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return prefs.getStringSet(SEARCH_HISTORY, emptySet())?.toList() ?: emptyList()
}

fun saveSearchHistory(context: Context, history: List<String>) {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    prefs.edit().putStringSet(SEARCH_HISTORY, history.toSet()).apply()
}

fun addCityToSearchHistory(context: Context, city: String): List<String> {
    val currentHistory = getSearchHistory(context).toMutableList()
    currentHistory.remove(city.lowercase(Locale.getDefault()))
    currentHistory.add(0, city.lowercase(Locale.getDefault()))
    val trimmed = currentHistory.take(5)
    saveSearchHistory(context, trimmed)
    return trimmed
}
fun removeCityFromSearchHistory(context: Context, city: String): List<String> {
    val currentHistory = getSearchHistory(context).toMutableList()
    currentHistory.remove(city.lowercase(Locale.getDefault()))
    val trimmed = currentHistory.take(5)
    saveSearchHistory(context, trimmed)
    return trimmed
}

data class WeatherData(
    val current: ApiDataCurrent? = null,
    val forecast: ApiDataForecast? = null,
    val city: String,
)
private const val FAVOURITES_SAVE = "favourites_save"

fun saveFavourites(context: Context, favourites: List<WeatherData>){
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val gson = Gson()
    val dataJson = gson.toJson(favourites)
    prefs.edit().putString(FAVOURITES_SAVE, dataJson).apply()
}

fun getFavourites(context: Context): List<WeatherData> {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val gson = Gson()
    val dataJson = prefs.getString(FAVOURITES_SAVE, null)

    return if (dataJson != null) {
        val type = object : TypeToken<List<WeatherData>>() {}.type
        gson.fromJson(dataJson, type) ?: emptyList()
    } else {
        emptyList()
    }
}

suspend fun addFavourite(context: Context, city: String): List<WeatherData> {
    val currentFavourites = getFavourites(context).toMutableList()
    currentFavourites.removeAll { it.city.equals(city, ignoreCase = true) }

    val repository = WeatherRepository()
    var currentData: ApiDataCurrent? = null
    var forecastData: ApiDataForecast? = null

    try {
        currentData = repository.getWeather(city, API_KEY)
        forecastData = repository.getForecast(city, API_KEY)
    } catch (e: Exception) {
    }

    val newFavourite = WeatherData(city = city, current = currentData, forecast = forecastData)
    currentFavourites.add(0, newFavourite)
    saveFavourites(context, currentFavourites)

    return currentFavourites
}
fun removeFavourite(context: Context, city: String): List<WeatherData> {
    val currentFavourites = getFavourites(context).toMutableList()
    currentFavourites.removeAll { it.city.lowercase(Locale.getDefault()) == city.lowercase(Locale.getDefault()) }
    saveFavourites(context, currentFavourites)
    return currentFavourites
}

fun List<WeatherData>.toCityList(): List<String> {
    val cityList = mutableListOf<String>()
    for (weatherData in this) {
        cityList.add(weatherData.city)
    }
    return cityList
}


private const val LAST_SAVE = "last_viewed_save"

fun saveLastViewedData(context: Context, city: String, currentWeather: ApiDataCurrent?, forecastWeather: ApiDataForecast?) {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val gson = Gson()

    val offlineData = WeatherData(city=city, current = currentWeather, forecast = forecastWeather)

    val dataJson = gson.toJson(offlineData)

    prefs.edit().putString(LAST_SAVE, dataJson).apply()
}

fun getLastViewedData(context: Context): WeatherData? {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val gson = Gson()

    val dataJson = prefs.getString(LAST_SAVE, null)

    return gson.fromJson(dataJson, WeatherData::class.java)
}

private const val UNITS = "units"
private const val AUTO_REFRESH = "auto_refresh"
private const val REFRESH_INTERVAL = "refresh_interval"

fun setUnitSystem(context: Context, unit: String) {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(UNITS, unit).apply()
}

fun getUnitSystem(context: Context): String {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return prefs.getString(UNITS, "metric") ?: "metric"
}

fun setAutoRefresh(context: Context, auto: Boolean) {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    prefs.edit().putBoolean(AUTO_REFRESH, auto).apply()
}

fun isAutoRefresh(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(AUTO_REFRESH, true)
}

fun setRefreshInterval(context: Context, minutes: Int) {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    prefs.edit().putInt(REFRESH_INTERVAL, minutes).apply()
}

fun getRefreshInterval(context: Context): Int {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return prefs.getInt(REFRESH_INTERVAL, 15)
}
