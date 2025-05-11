package com.example.weather_app.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherDetails(
    num: Int,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconColor: Color = Color.White
){
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically){
        Icon(imageVector = icon, tint = iconColor, modifier = Modifier.size(23.dp), contentDescription = null)

        Spacer(modifier = Modifier.width(4.dp))

        Text(num.toString(), color = Color.White, fontSize = 14.sp)

        Spacer(modifier = Modifier.width(2.dp))

        Text(unit, color = Color.White, fontSize = 14.sp)
    }
}