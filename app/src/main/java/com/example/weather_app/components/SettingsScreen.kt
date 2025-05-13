package com.example.weather_app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather_app.api.WeatherView
import com.example.weather_app.util.getOutlinedInputColors
import com.example.weather_app.util.getRefreshInterval
import com.example.weather_app.util.getUnitSystem
import com.example.weather_app.util.gradientBackgroundBrush
import com.example.weather_app.util.isAutoRefresh
import com.example.weather_app.util.mainGradientColors
import com.example.weather_app.util.setAutoRefresh
import com.example.weather_app.util.setRefreshInterval
import com.example.weather_app.util.setUnitSystem
import com.example.weather_app.util.toast

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val viewModel: WeatherView = viewModel()


    var unitSystem by remember { mutableStateOf(getUnitSystem(context)) }
    var refreshInterval by remember { mutableStateOf(getRefreshInterval(context)) }
    var autoRefresh by remember { mutableStateOf(isAutoRefresh(context)) }

    var intervalField by remember { mutableStateOf(getRefreshInterval(context).toString()) }

    LaunchedEffect(Unit) {
        unitSystem = getUnitSystem(context)
        refreshInterval = getRefreshInterval(context)
        autoRefresh = isAutoRefresh(context)
    }

    Box(
        modifier = Modifier.background(brush = gradientBackgroundBrush(colors = mainGradientColors)).padding(10.dp)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text("Settings", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))

            Spacer(Modifier.height(20.dp))

            Text("Units", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 20.sp)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                RadioButton(
                    selected = unitSystem == "metric",
                    onClick = {
                        unitSystem = "metric"
                        setUnitSystem(context, "metric")
                    }, colors = RadioButtonDefaults.colors(selectedColor = Color.White, unselectedColor = Color.Gray)
                )
                Text("Metric", color = Color.White, fontSize = 16.sp)

                Spacer(Modifier.width(12.dp))

                RadioButton(
                    selected = unitSystem == "imperial",
                    onClick = {
                        unitSystem = "imperial"
                        setUnitSystem(context, "imperial")
                    }, colors = RadioButtonDefaults.colors(selectedColor = Color.White, unselectedColor = Color.Gray)
                )
                Text("Imperial", color = Color.White, fontSize = 16.sp)
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Auto-refresh favourites", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 22.sp)
                Switch(checked = autoRefresh,
                    onCheckedChange = {
                        autoRefresh = it
                        setAutoRefresh(context, it)
                        if (it) {
                            refreshInterval = 5
                        }
                    }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, uncheckedThumbColor = Color.Black, checkedTrackColor = Color.Gray, uncheckedTrackColor = Color.LightGray)
                )
            }

            if (!autoRefresh) {
                Spacer(modifier = Modifier.height(12.dp))
                Column {
                    Text(
                        "Set refresh interval (minutes)",
                        fontWeight = FontWeight.Light,
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = intervalField,
                            onValueChange = {
                                intervalField = it.filter { char -> char.isDigit() }
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(2f).heightIn(min = 56.dp),
                            colors = getOutlinedInputColors(),
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = {
                                val newRefresh = intervalField.toIntOrNull()
                                if (newRefresh != null) {
                                    if (newRefresh in 1..10000) {
                                        refreshInterval = newRefresh
                                        setRefreshInterval(context, newRefresh)
                                        intervalField = newRefresh.toString()
                                    }
                                } else {
                                    context.toast("Incorrect refresh value")
                                }
                            },modifier = Modifier.weight(1f).padding(6.dp).heightIn(min = 56.dp),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Color.Transparent,
                            ),
                        ) {
                            Text("Set")
                        }
                    }
                }
            }

            Spacer(Modifier.height(64.dp))

            OutlinedButton(
                onClick = { viewModel.refreshFavoritesDataAndDisplayToast(context) },
                modifier = Modifier.align(Alignment.CenterHorizontally).heightIn(min = 56.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color.Transparent,
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")

                Spacer(Modifier.width(8.dp))

                Text("Refresh favourites manually", fontSize = 20.sp)
            }
        }
    }
}
