package com.example.weather_app.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.weather_app.util.Screen
import com.example.weather_app.util.bottomNavGradientColors
import com.example.weather_app.util.gradientBackgroundBrush

@Composable
fun BottomNavigationBar(navController: NavController, items: List<Screen>) {
    val currentBackStack = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack.value?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradientBackgroundBrush(colors = bottomNavGradientColors))
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent
        ) {
            items.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            screen.icon,
                            contentDescription = screen.title,
                            tint = Color.White
                        )
                    },
                    label = { Text(screen.title, color = Color.White) },
                    selected = currentRoute?.startsWith(screen.route) == true,
                    onClick = {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Gray
                    ))
            }
        }
    }
}


