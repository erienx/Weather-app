package com.example.weather_app.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.weather_app.R
import com.example.weather_app.api.ApiDataCurrent
import com.example.weather_app.util.cardGradientColors
import com.example.weather_app.util.gradientBackgroundBrush
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeatherCard(
    data: ApiDataCurrent,
    modifier: Modifier = Modifier,
    backgroundColors: List<Color> = cardGradientColors,
){
    Card(
        modifier = modifier.fillMaxWidth().padding(14.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    )
    {
        Box(modifier = Modifier
            .background(brush = gradientBackgroundBrush(colors = backgroundColors, isVerticalGradient = false))
            .padding(14.dp)) {
            Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        "${data.name}, ${data.sys.country}",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(getRefreshedTimeString(data.dt), color = Color.White, fontSize = 14.sp)
                }


                val iconUrl = "https://openweathermap.org/img/wn/${data.weather.firstOrNull()?.icon}@4x.png"
                Image(
                    painter = rememberAsyncImagePainter(iconUrl),
                    contentDescription = "weather icon",
                    modifier = Modifier.size(120.dp)
                )

                Text("${data.main.temp.roundToInt()}°C",color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(2.dp))

                Text(data.weather.firstOrNull()?.description?.capitalize() ?: "",  color = Color.White,  fontSize = 18.sp)

                Spacer(modifier = Modifier.height(48.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){
                    WeatherDetails(num = data.main.pressure, unit = "hpa", icon = ImageVector.vectorResource(id = R.drawable.ic_pressure))
                    WeatherDetails(num = data.main.humidity, unit = "%", icon = ImageVector.vectorResource(id = R.drawable.ic_drop))
                    WeatherDetails(num = data.wind.speed.roundToInt(), unit = "km/h", icon = ImageVector.vectorResource(id = R.drawable.ic_wind))
                }
            }
        }
    }

}
fun String.capitalize(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

fun getRefreshedTimeString(dt:Long): String{
    val date = Date(dt * 1000L)
    val currentDate = Date()

    val timeDiffMili = currentDate.time - date.time
    val daysDiff = timeDiffMili / (1000 * 60 * 60 * 24)

    val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeFormatted = sdfTime.format(date)

    val dateFormatted: String = when {
        daysDiff == 0L -> "Today $timeFormatted"
        daysDiff == 1L -> "Yesterday $timeFormatted"
        else -> "$daysDiff days ago $timeFormatted"
    }

    return "Refreshed $dateFormatted"
}
