package com.example.weather_app.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weather_app.api.WeatherView
import com.example.weather_app.util.Screen
import com.example.weather_app.util.getFavourites
import com.example.weather_app.util.gradientBackgroundBrush
import com.example.weather_app.util.mainGradientColors
import com.example.weather_app.util.removeFavourite
import com.example.weather_app.util.toCityList
import com.example.weather_app.util.toast

@Composable
fun FavouritesScreen(navController: NavController) {
    val context = LocalContext.current
    var favourites by remember { mutableStateOf(getFavourites(context)) }
    val viewModel: WeatherView = viewModel()

    Box(
        modifier = Modifier.fillMaxSize().background(brush = gradientBackgroundBrush(colors = mainGradientColors)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            Button(onClick = {
                viewModel.refreshFavoritesDataAndDisplayToast(context)
            }) { Text("refresh favourites") }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Your favourites", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.height(12.dp))

            if (favourites.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text( "No favourites added",  color = Color.White,  fontSize = 20.sp,  fontWeight = FontWeight.SemiBold)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(favourites.toCityList()) { city ->
                        FavouriteItem(city = city, onRemove = {
                            favourites = getFavourites(context)
                        }, onClick = {
                            navController.navigate("weather/$city") {
                                popUpTo(Screen.Search.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun FavouriteItem(city: String, onRemove: () -> Unit, onClick: () -> Unit) {
    val context = LocalContext.current
    var favourites by remember { mutableStateOf(getFavourites(context)) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.1f)).clickable{onClick()}.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = "favourite icon", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = city.capitalize(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Medium)
        }

        Button(
            onClick = {
                removeFavourite(context, city)
                context.toast("Removed ${city.capitalize()} from favourites")
                favourites = getFavourites(context)
                onRemove()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "remove from favourites", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(24.dp))
        }
    }
}
