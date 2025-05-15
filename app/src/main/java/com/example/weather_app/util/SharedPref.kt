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

fun getSearchHistory(context: Context): List<LocationData> {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val json = prefs.getString(SEARCH_HISTORY, null)
    return if (json != null) {
        val type = object : TypeToken<List<LocationData>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}

fun saveSearchHistory(context: Context, history: List<LocationData>) {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val json = Gson().toJson(history)
    prefs.edit().putString(SEARCH_HISTORY, json).apply()
}

fun addToSearchHistory(context: Context, location: LocationData): List<LocationData> {
    if (location.city.isBlank()) {
        return getSearchHistory(context)
    }

    val currentHistory = getSearchHistory(context).toMutableList()
    currentHistory.removeAll { it.lat == location.lat && it.lon == location.lon }
    currentHistory.add(0, location)
    val trimmed = currentHistory.take(5)
    saveSearchHistory(context, trimmed)
    return trimmed
}
fun removeFromSearchHistory(context: Context, location: LocationData): List<LocationData> {
    val currentHistory = getSearchHistory(context).toMutableList()
    currentHistory.removeAll { it.city.equals(location.city, ignoreCase = true) }
    val trimmed = currentHistory.take(5)
    saveSearchHistory(context, trimmed)
    return trimmed
}

data class WeatherData(
    val current: ApiDataCurrent? = null,
    val forecast: ApiDataForecast? = null,
    val location: LocationData
)
private const val FAVOURITES_SAVE = "favourites_save"

fun saveFavourites(context: Context, favourites: List<WeatherData>) {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val json = Gson().toJson(favourites)
    prefs.edit().putString(FAVOURITES_SAVE, json).apply()
}

fun getFavourites(context: Context): List<WeatherData> {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val json = prefs.getString(FAVOURITES_SAVE, null)
    return if (json != null) {
        val type = object : TypeToken<List<WeatherData>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}

suspend fun addFavourite(context: Context, location: LocationData): List<WeatherData> {
    val currentFavourites = getFavourites(context).toMutableList()
    currentFavourites.removeAll { it.location.lat == location.lat && it.location.lon == location.lon }

    val repository = WeatherRepository()
    var currentData: ApiDataCurrent? = null
    var forecastData: ApiDataForecast? = null

    try {
        currentData = repository.getCurrentByCoords(location.lat, location.lon, API_KEY)
        forecastData = repository.getForecastByCoords(location.lat, location.lon, API_KEY)
    } catch (e: Exception) {
    }

    val newFavourite = WeatherData(location = location, current = currentData, forecast = forecastData)
    currentFavourites.add(0, newFavourite)
    saveFavourites(context, currentFavourites)
    return currentFavourites
}
fun removeFavourite(context: Context, location: LocationData): List<WeatherData> {
    val currentFavourites = getFavourites(context).toMutableList()
    currentFavourites.removeAll { it.location.lat == location.lat && it.location.lon == location.lon }
    saveFavourites(context, currentFavourites)
    return currentFavourites
}

fun List<WeatherData>.toCityList(): List<String> {
    val cityList = mutableListOf<String>()
    for (weatherData in this) {
        cityList.add(weatherData.location.city)
    }
    return cityList
}


private const val LAST_SAVE = "last_viewed_save"

fun saveLastViewedData(context: Context, location: LocationData, current: ApiDataCurrent?, forecast: ApiDataForecast?) {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val data = WeatherData(location = location, current = current, forecast = forecast)
    val json = Gson().toJson(data)
    prefs.edit().putString(LAST_SAVE, json).apply()
}

fun getLastViewedData(context: Context): WeatherData? {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val json = prefs.getString(LAST_SAVE, null)
    return Gson().fromJson(json, WeatherData::class.java)
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

const val LAST_REFRESH = "last_refresh"

fun saveLastRefresh(context: Context, timestamp: Long) {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    prefs.edit().putLong(LAST_REFRESH, timestamp).apply()
}

fun getLastRefresh(context: Context): Long {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return prefs.getLong(LAST_REFRESH, 0)
}
