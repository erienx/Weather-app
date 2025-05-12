package com.example.weather_app.util

import android.content.Context
import com.example.weather_app.api.ApiDataCurrent
import com.example.weather_app.api.ApiDataForecast
import com.google.gson.Gson
import java.util.Locale

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
    currentHistory.remove(city.toLowerCase())
    currentHistory.add(0, city.toLowerCase())
    val trimmed = currentHistory.take(5)
    saveSearchHistory(context, trimmed)
    return trimmed
}
fun removeCityFromSearchHistory(context: Context, city: String): List<String> {
    val currentHistory = getSearchHistory(context).toMutableList()
    currentHistory.remove(city.toLowerCase())
    val trimmed = currentHistory.take(5)
    saveSearchHistory(context, trimmed)
    return trimmed
}


private const val FAVOURITES = "favourites"

fun saveFavourites(context: Context, favs: List<String>){
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    prefs.edit().putStringSet(FAVOURITES, favs.toSet()).apply()
}
fun getFavourites(context: Context): List<String>{
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return prefs.getStringSet(FAVOURITES, emptySet())?.toList()?:emptyList()
}
fun addFavourite(context: Context, fav: String): List<String>{
    val currentFavs = getFavourites(context).toMutableList()
    currentFavs.remove(fav.toLowerCase())
    currentFavs.add(0, fav.toLowerCase())
    saveFavourites(context, currentFavs)
    return currentFavs
}
fun removeFavourite(context: Context, fav: String): List<String>{
    val currentFavs = getFavourites(context).toMutableList()
    currentFavs.remove(fav.toLowerCase())
    saveFavourites(context, currentFavs)
    return currentFavs
}

fun String.toLowerCase(): String {
    return this.lowercase(Locale.getDefault())
}




data class WeatherData(
    val current: ApiDataCurrent? = null,
    val forecast: ApiDataForecast? = null,
    val city: String,
)

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

private const val FAVOURITES_SAVE = "favourites_save"
