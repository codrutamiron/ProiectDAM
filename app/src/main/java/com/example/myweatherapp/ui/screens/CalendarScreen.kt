package com.example.myweatherapp.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myweatherapp.logic.ForecastResponse
import com.example.myweatherapp.logic.PreferencesManager
import com.example.myweatherapp.logic.RetrofitClient
import com.example.myweatherapp.ui.ForecastItem

@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val location = remember { mutableStateOf("") }
    val forecast = remember { mutableStateOf<ForecastResponse?>(null) }

    // Fetch forecast when the screen loads
    LaunchedEffect(Unit) {
        location.value = preferencesManager.getLocation()
        try {
            forecast.value = RetrofitClient.get5DayForecast(location.value)
        } catch (e: Exception) {
            forecast.value = null
            Toast.makeText(
                context,
                "Location not found. Search for another or use current location.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold { innerPadding ->
        forecast.value?.let { forecastResponse ->
            // Group forecasts by day
            val groupedForecasts = forecastResponse.list.groupBy {
                val sdf = java.text.SimpleDateFormat("EEE, MMM d", java.util.Locale.getDefault())
                sdf.format(java.util.Date(it.dt * 1000L)) // Convert dt (in seconds) to Date
            }

            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(0.dp, 8.dp, 0.dp, 0.dp)
                    .fillMaxSize()
            ) {
                groupedForecasts.forEach { (date, dailyForecasts) ->
                    item {
                        // Display the date as a header
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(8.dp, 16.dp)
                        )
                    }
                    items(dailyForecasts) { forecastItem ->
                        ForecastItem(forecastItem)
                    }
                }
            }
        } ?: Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }
}
