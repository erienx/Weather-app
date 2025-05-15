package com.example.weather_app.components


import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weather_app.api.ApiDataGeocoding
import com.example.weather_app.api.WeatherView
import com.example.weather_app.ui.theme.DarkBlue3
import com.example.weather_app.util.LocationData
import com.example.weather_app.util.Screen
import com.example.weather_app.util.addFavourite
import com.example.weather_app.util.addToSearchHistory
import com.example.weather_app.util.getFavourites
import com.example.weather_app.util.getOutlinedInputColors
import com.example.weather_app.util.getSearchHistory
import com.example.weather_app.util.gradientBackgroundBrush
import com.example.weather_app.util.mainGradientColors
import com.example.weather_app.util.removeFavourite
import com.example.weather_app.util.removeFromSearchHistory
import com.example.weather_app.util.toCityList
import com.example.weather_app.util.toast
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(navController: NavController, onLocationSelected: ((LocationData) -> Unit)? = null, onFavouriteToggled: () -> Unit = {}) {
    val context = LocalContext.current
    var searchHistory by remember { mutableStateOf(getSearchHistory(context)) }
    val viewModel: WeatherView = viewModel()
    val citySuggestions by viewModel.citySuggestions.collectAsState()

    fun redirectToLocation(location: LocationData) {
        val updatedHistory = addToSearchHistory(context, location)
        searchHistory = updatedHistory
        onLocationSelected?.invoke(location) ?: navController.navigate("weather/${location.lat}/${location.lon}") {
            popUpTo(Screen.Search.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(brush = gradientBackgroundBrush(colors = mainGradientColors))) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
//            Text("Hello Mati!!!",color = Color.Red, fontSize = 94.sp)
//            Spacer(modifier = Modifier.height(16.dp))
//            Text("Miłego dnia!!!",color = Color.Magenta, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Search for a City", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            SearchBar { query -> viewModel.setSearchQuery(query) }

            if (citySuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Suggestions", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                citySuggestions.forEach { suggestion ->
                    val locationText = getLocationText(suggestion)
                    val location = LocationData(city = locationText, lat = suggestion.lat, lon = suggestion.lon)
                    SuggestionItem(suggestion = suggestion) {
                        redirectToLocation(location)
                    }
                }
            }

            if (searchHistory.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Recent searches", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))

                searchHistory.forEach { location ->
                    SearchHistoryItem(location = location, onClick = { redirectToLocation(location) }, onRemove = { searchHistory = getSearchHistory(context) }, onFavouriteToggled = onFavouriteToggled)
                }
            }
        }
    }
}
fun getLocationText(data: ApiDataGeocoding): String {
    val locationText = buildString {
        append(data.name)
        if (data.state != null) {
            append(", ${data.state}")
        }
        append(", ${data.country}")
    }
    return locationText
}


@Composable
fun SuggestionItem(suggestion: ApiDataGeocoding, onClick: () -> Unit) {
    val locationText = getLocationText(suggestion)

    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.15f)).clickable { onClick() }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(24.dp))

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) { Text(text = locationText, color = Color.White, fontSize = 16.sp)
            Text( text = "Lat: ${"%.2f".format(suggestion.lat)}, Lon: ${"%.2f".format(suggestion.lon)}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun SearchBar(onSearchQueryChange: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                isError = false
                onSearchQueryChange(it.trim())
            },
            textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
            colors = getOutlinedInputColors(),
            placeholder = { Text("Enter city name...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "search") },
            singleLine = true,
            isError = isError,
            supportingText = if (isError) {
                { Text("Enter a valid name", color = MaterialTheme.colorScheme.error) }
            } else null,
            modifier = Modifier.fillMaxWidth(),

            )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
@Composable
fun SearchHistoryItem(location: LocationData, onClick: () -> Unit, onRemove: () -> Unit, onFavouriteToggled: () -> Unit = {}) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var favourites by remember { mutableStateOf(getFavourites(context)) }
    val isCityInFavourites = favourites.any { it.location == location }

    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.1f)).clickable { onClick() }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon( imageVector = Icons.Default.DateRange,  contentDescription = "History",  tint = Color.White.copy(alpha = 0.8f),  modifier = Modifier.size(24.dp) )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = location.city.capitalize(), color = Color.White, fontSize = 16.sp)
        }

        IconButton(
            onClick = {
                removeFromSearchHistory(context, location)
                onRemove() }
        ) { Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove from history", tint = Color.White.copy(alpha = 0.8f)) }

        IconButton(onClick = {
            coroutineScope.launch {
                if (isCityInFavourites) {
                    removeFavourite(context, location)
                    context.toast("Removed ${location.city.capitalize()} from favourites")
                } else {
                    addFavourite(context, location)
                    context.toast("Added ${location.city.capitalize()} to favourites")
                }
                favourites = getFavourites(context)
                onFavouriteToggled()

            }
        }) {
            Icon( imageVector = if (isCityInFavourites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,  contentDescription = "Add to favourites",  tint = Color.White )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

