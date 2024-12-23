package com.example.myweatherapp.ui.screens

import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myweatherapp.logic.GeocodingResponse
import com.example.myweatherapp.logic.PreferencesManager
import com.example.myweatherapp.logic.RetrofitClient
import com.example.myweatherapp.logic.WeatherResponse
import com.example.myweatherapp.ui.SunriseSunset
import com.example.myweatherapp.ui.WeatherIcon
import com.example.myweatherapp.ui.WeatherInfo
import com.example.myweatherapp.ui.WeatherLocation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CurrentWeatherScreen() {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val location = remember { mutableStateOf("Cluj") }
    val currentWeather = remember { mutableStateOf<WeatherResponse?>(null) }

    LaunchedEffect(location) {
        preferencesManager.getLocation().let { loc ->
            location.value = loc
            try {
                currentWeather.value = RetrofitClient.getCurrentWeather(loc)
            } catch (e: Exception) {
                currentWeather.value = null
                Toast.makeText(
                    context,
                    "Location not found. Search for another or use current location.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Scaffold { innerPadding ->
        currentWeather.value?.let {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WeatherLocation(it.name)
                WeatherIcon(it.weather[0].icon, it.weather[0].description)
                WeatherInfo(it.main.temp, it.main.feels_like, it.main.humidity, it.main.pressure)
                SunriseSunset(it.sys.sunrise, it.sys.sunset)
            }
        } ?: Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }
}