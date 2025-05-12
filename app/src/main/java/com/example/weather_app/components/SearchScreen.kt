package com.example.weather_app.components


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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.navigation.NavController
import com.example.weather_app.ui.theme.DarkBlue3
import com.example.weather_app.util.Screen
import com.example.weather_app.util.addCityToSearchHistory
import com.example.weather_app.util.addFavourite
import com.example.weather_app.util.getFavourites
import com.example.weather_app.util.getSearchHistory
import com.example.weather_app.util.gradientBackgroundBrush
import com.example.weather_app.util.mainGradientColors
import com.example.weather_app.util.removeCityFromSearchHistory
import com.example.weather_app.util.removeFavourite
import com.example.weather_app.util.toCityList
import com.example.weather_app.util.toast
import kotlinx.coroutines.launch


@Composable
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current
    var searchHistory by remember { mutableStateOf(getSearchHistory(context)) }

    fun redirectToCity(city: String) {
        val updatedHistory = addCityToSearchHistory(context, city)
        searchHistory = updatedHistory
        navController.navigate("weather/$city") {
            popUpTo(Screen.Search.route) { inclusive = true }
            launchSingleTop = true
        }

    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackgroundBrush(colors = mainGradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
//        Text("Hello Mati!!!",color = Color.Red, fontSize = 94.sp)
//            Spacer(modifier = Modifier.height(16.dp))
//            Text("Miłego dnia!!!",color = Color.Magenta, fontSize = 48.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Search for a City",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            SearchBar(onSearch = { city -> redirectToCity(city) }
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (searchHistory.isNotEmpty()) {
                Text(
                    "Recent searches",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn {
                    items(searchHistory) { city ->
                        SearchHistoryItem(city = city, onClick = {
                            redirectToCity(city)
                        }, onRemove = {
                            searchHistory = getSearchHistory(context)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                isError = false
            },
            textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                focusedLeadingIconColor = Color.White,
                unfocusedLeadingIconColor = Color.LightGray,
                unfocusedTextColor = Color.Gray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                disabledBorderColor = Color.Transparent,
                focusedPlaceholderColor = Color.LightGray,
                unfocusedPlaceholderColor = Color.Gray,
                disabledPlaceholderColor = Color.Transparent,
                errorPlaceholderColor = Color.Red,
            ),
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

        Button(
            onClick = {
                if (searchQuery.trim().length < 2) {
                    isError = true
                } else {
                    onSearch(searchQuery.trim())
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkBlue3,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text( "Search",  fontSize = 22.sp,  fontWeight = FontWeight.SemiBold,  color = Color.White
            )
        }
    }
}

@Composable
fun SearchHistoryItem(city: String, onClick: () -> Unit, onRemove: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var favourites by remember { mutableStateOf(getFavourites(context)) }
    val isCityInFavourites = favourites.toCityList().contains(city)

    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.1f)).clickable { onClick() }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon( imageVector = Icons.Default.DateRange,  contentDescription = "History",  tint = Color.White.copy(alpha = 0.8f),  modifier = Modifier.size(24.dp) )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = city.capitalize(), color = Color.White, fontSize = 16.sp)
        }

        IconButton(
            onClick = {
                removeCityFromSearchHistory(context, city)
                onRemove()
            }
        ) { Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove from history", tint = Color.White.copy(alpha = 0.8f)) }

        IconButton(onClick = {
            coroutineScope.launch {
                if (isCityInFavourites) {
                    removeFavourite(context, city)
                    context.toast("Removed ${city.capitalize()} from favourites")
                } else {
                    addFavourite(context, city)
                    context.toast("Added ${city.capitalize()} to favourites")
                }
                favourites = getFavourites(context)
            }
        }) {
            Icon( imageVector = if (isCityInFavourites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,  contentDescription = "Add to favourites",  tint = Color.White )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
