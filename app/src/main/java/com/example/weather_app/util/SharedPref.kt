package com.example.weather_app.util

import android.content.Context

private const val PREF_NAME = "weather_prefs"



private const val KEY_SEARCH_HISTORY = "search_history"

fun getSearchHistory(context: Context): List<String> {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return prefs.getStringSet(KEY_SEARCH_HISTORY, emptySet())?.toList() ?: emptyList()
}

fun saveSearchHistory(context: Context, history: List<String>) {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    prefs.edit().putStringSet(KEY_SEARCH_HISTORY, history.toSet()).apply()
}

fun addCityToSearchHistory(context: Context, city: String): List<String> {
    val currentHistory = getSearchHistory(context).toMutableList()
    currentHistory.remove(city)
    currentHistory.add(0, city)
    val trimmed = currentHistory.take(5)
    saveSearchHistory(context, trimmed)
    return trimmed
}


private const val KEY_FAVOURITES = "favourites"

fun saveFavourites(context: Context, favs: List<String>){
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    prefs.edit().putStringSet(KEY_FAVOURITES, favs.toSet()).apply()
}
fun getFavourites(context: Context): List<String>{
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return prefs.getStringSet(KEY_FAVOURITES, emptySet())?.toList()?:emptyList()
}
fun addFavourite(context: Context, fav: String): List<String>{
    val currentFavs = getFavourites(context).toMutableList()
    currentFavs.remove(fav)
    currentFavs.add(0, fav)
    saveFavourites(context, currentFavs)
    return currentFavs
}
fun removeFavourite(context: Context, fav: String): List<String>{
    val currentFavs = getFavourites(context).toMutableList()
    currentFavs.remove(fav)
    saveFavourites(context, currentFavs)
    return currentFavs
}