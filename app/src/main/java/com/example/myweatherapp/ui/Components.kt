package com.example.myweatherapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.example.myweatherapp.logic.LocationManager
import com.example.myweatherapp.logic.RetrofitClient
import com.example.myweatherapp.logic.WeatherData
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ForecastItem(dailyForecast: WeatherData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hour
            val hour = remember(dailyForecast) {
                val sdf = SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                sdf.format(Date(dailyForecast.dt * 1000L))
            }
            Text(
                text = hour,
                style = MaterialTheme.typography.titleMedium
            )

            // Temperature
            Text(
                text = "${dailyForecast.main.temp}°C",
                style = MaterialTheme.typography.titleMedium
            )

            // Weather Icon
            Image(
                painter = rememberAsyncImagePainter(
                    model = "https://openweathermap.org/img/wn/${dailyForecast.weather[0].icon}@2x.png"
                ),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute(navController) == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.DateRange, contentDescription = "Calendar") },
            label = { Text("Calendar") },
            selected = currentRoute(navController) == "calendar",
            onClick = { navController.navigate("calendar") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = currentRoute(navController) == "search",
            onClick = { navController.navigate("search") }
        )
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun WeatherLocation(locationName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = locationName,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
fun WeatherInfo(temp: Double, feelsLike: Double, humidity: Int, pressure: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoTile("Temperature", "${temp}°C")
        InfoTile("Feels like", "${feelsLike}°C")
        InfoTile("Humidity", "$humidity%")
        InfoTile("Pressure", "$pressure hPa")
    }
}

@Composable
fun InfoTile(label: String, value: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun WeatherIcon(icon: String, weatherDescription: String) {
    val imageUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
    val title = weatherDescription.split(" ")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .size(120.dp), // Adjust the size to accommodate both the icon and text
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // Weather Icon
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Weather Icon",
                modifier = Modifier
//                    .align(Alignment.Center)
                    .size(72.dp)
            )

            // Weather Description Text
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
//                    .align(Alignment.BottomCenter) // Align the text at the bottom of the Box
                    .padding(bottom = 8.dp) // Add some padding from the bottom
            )
        }
    }
}


@Composable
fun SunriseSunset(sunrise: Long, sunset: Long) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoTile("Sunrise", formatTimestamp(sunrise))
        InfoTile("Sunset", formatTimestamp(sunset))
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

@Composable
fun ErrorMessage(errorMessage: String) {
    if (errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun LocationInputField(location: String, onValueChange: (String) -> Unit) {
    TextField(
        value = location,
        onValueChange = onValueChange,
        label = { Text("Enter City Name") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun SaveLocationButton(location: String, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        enabled = location.isNotEmpty(),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("Set Location")
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CurrentLocationButton(
    isLoading: Boolean,
    locationPermissionState: PermissionState,
    onLoading: () -> Unit,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit,
    locationManager: LocationManager,
    scope: CoroutineScope
) {
    ExtendedFloatingActionButton(
        onClick = {
            onLoading()
            scope.launch {
                if (locationPermissionState.status is PermissionStatus.Granted) {
                    val currentLocation = locationManager.getCurrentLocation()
                    if (currentLocation != null) {
                        val cityName = RetrofitClient.getCityName(
                            currentLocation.latitude,
                            currentLocation.longitude
                        ).name
                        onSuccess(cityName)
                    } else {
                        onError("Unable to retrieve location. Try again.")
                    }
                } else {
                    locationPermissionState.launchPermissionRequest()
                }
            }
        },
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Text("Loading...")
            }
        } else {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Use Current Location")
        }
    }
}

@Composable
fun TriggerNotificationButton(onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            imageVector = Icons.Rounded.Notifications,
            contentDescription = "Trigger Notification",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("Trigger Notification")
    }
}
