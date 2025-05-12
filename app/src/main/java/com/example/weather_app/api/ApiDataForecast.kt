package com.example.weather_app.api


data class ApiDataForecast(
    val city: CityForecast,
    val cnt: Int,
    val cod: String,
    val list: List<ItemForecast>,
    val message: Int
)
data class CityForecast(
    val coord: CoordForecast,
    val country: String,
    val id: Int,
    val name: String,
    val population: Int,
    val sunrise: Int,
    val sunset: Int,
    val timezone: Int
)
data class CloudsForecast(
    val all: Int
)
data class CoordForecast(
    val lat: Double,
    val lon: Double
)
data class ItemForecast(
    val clouds: CloudsForecast,
    val dt: Int,
    val dt_txt: String,
    val main: MainForecast,
    val pop: Double,
    val rain: RainForecast,
    val sys: SysForecast,
    val visibility: Int,
    val weather: List<WeatherForecast>,
    val wind: WindForecast
)
data class MainForecast(
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: Double,
    val temp_kf: Double,
    val temp_max: Double,
    val temp_min: Double
)
data class RainForecast(
    val `3h`: Double
)
data class SysForecast(
    val pod: String
)
data class WeatherForecast(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)
data class WindForecast(
    val deg: Int,
    val gust: Double,
    val speed: Double
)