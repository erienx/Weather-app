package com.example.weather_app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.weather_app.R
import com.example.weather_app.api.ApiDataForecast
import com.example.weather_app.api.ItemForecast
import com.example.weather_app.util.getUnitSystem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ForecastList(forecast: ApiDataForecast) {
    val groupedForecast = processForecasts(forecast.list)

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(color = Color.White.copy(alpha = 0.1f))) {
        LazyColumn {
            items(groupedForecast) { dayForecast ->
                ForecastDayRow(dayForecast)
            }
        }
    }
}

data class ForecastData(
    val date: String,
    val dayIcon: String,
    val nightIcon: String,
    val maxTemp: Double,
    val minTemp: Double,
    val humidity: Int
)

fun processForecasts(forecastList: List<ItemForecast>): List<ForecastData> {
    //    "dt_txt": "2022-08-30 15:00:00"
    val forecastsByDate = forecastList.groupBy { forecast ->
        val dateTime = forecast.dt_txt  .split(" ")[0]
        dateTime.split(" ")[0]
    }
    val result = mutableListOf<ForecastData>()

    for ((dateString, forecastDay) in forecastsByDate) {
        val max = forecastDay.maxOfOrNull { it.main.temp_max } ?: 0.0
        val min = forecastDay.minOfOrNull { it.main.temp_min } ?: 0.0

        var totalHumidity = 0
        var count = 0
        for (forecast in forecastDay) {
            totalHumidity += forecast.main.humidity
            count++
        }
        val hum = if (count > 0) totalHumidity / count else 0

        val dayForecast = forecastDay.find { it.dt_txt.contains("15:00") }
        val nightForecast = forecastDay.find { it.dt_txt.contains("00:00") }
        val dayIcon = dayForecast?.weather?.firstOrNull()?.icon ?: "01d"
        val nightIcon = nightForecast?.weather?.firstOrNull()?.icon ?: "01n"



        val input = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val output = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
        var day = output.format(LocalDate.parse(dateString, input))
        val currentDay = LocalDate.now().format(output)

        if (currentDay.equals(day)) {
            day = "Today"
        }


        val dailyForecast = ForecastData(date = day, dayIcon = dayIcon, nightIcon = nightIcon, maxTemp = max, minTemp = min, humidity = hum)
        result.add(dailyForecast)
    }

    return result
}

@Composable
fun ForecastDayRow(forecast: ForecastData) {
    val context = LocalContext.current
    val units = getUnitSystem(context)

    val minTemp = if (units == "imperial") {
        ((forecast.minTemp * 9 / 5) + 32).roundToInt()
    } else {
        forecast.minTemp.roundToInt()
    }
    val maxTemp = if (units == "imperial") {
        ((forecast.maxTemp * 9 / 5) + 32).roundToInt()
    } else {
        forecast.maxTemp.roundToInt()
    }
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = forecast.date, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
            Icon(painter = painterResource(id = R.drawable.ic_drop), contentDescription = "Humidity", tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${forecast.humidity}%", color = Color.White, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))

            var iconUrl = "https://openweathermap.org/img/wn/${forecast.dayIcon}@2x.png"
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(iconUrl).crossfade(true).build(),
                contentDescription = "Weather Icon", modifier = Modifier.size(40.dp), contentScale = ContentScale.Fit)

            iconUrl = "https://openweathermap.org/img/wn/${forecast.nightIcon}@2x.png"
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(iconUrl).crossfade(true).build(),
                contentDescription = "Weather Icon", modifier = Modifier.size(40.dp), contentScale = ContentScale.Fit)

            Text(text = "${minTemp}° ${maxTemp}°", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.width(60.dp))
        }
    }
}
