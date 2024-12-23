package com.example.myweatherapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.myweatherapp.logic.LocationManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myweatherapp.logic.LocationManager.Companion.PERMISSION_REQUEST_CODE
import com.example.myweatherapp.logic.PreferencesManager
import com.example.myweatherapp.logic.RetrofitClient
import com.example.myweatherapp.logic.WeatherNotificationWorker
import com.example.myweatherapp.logic.WeatherResponse
import com.example.myweatherapp.logic.requestLocationPermission
import com.example.myweatherapp.ui.CurrentLocationButton
import com.example.myweatherapp.ui.ErrorMessage
import com.example.myweatherapp.ui.LocationInputField
import com.example.myweatherapp.ui.SaveLocationButton
import com.example.myweatherapp.ui.TriggerNotificationButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

//@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(navController: NavHostController) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    var location by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val locationManager = LocationManager(context)

    val currentWeather = remember { mutableStateOf<WeatherResponse?>(null) }

    LaunchedEffect(Unit) {
        location = preferencesManager.getLocation()
        try {
            currentWeather.value = RetrofitClient.getCurrentWeather(location)
        } catch (e: Exception) {
            currentWeather.value = null
            Toast.makeText(
                context,
                "Location not found. Search for another or use current location.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            CurrentLocationButton(
                isLoading = isLoading,
                locationPermissionState = locationPermissionState,
                onLoading = { isLoading = true; errorMessage = "" },
                onSuccess = {
                    location = it
                    preferencesManager.setLocation(location)
                    isLoading = false
                    navController.navigate("home")
                },
                onError = {
                    isLoading = false
                    errorMessage = it
                },
                locationManager = locationManager,
                scope = scope
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ErrorMessage(errorMessage)
                    LocationInputField(location) { location = it }
                    SaveLocationButton(location) {
                        preferencesManager.setLocation(location)
                        navController.navigate("home")
                    }
                    TriggerNotificationButton {
                        requestLocationPermission(context as Activity)

                        val inputData = Data.Builder()
                            .putString(
                                "weather",
                                "${currentWeather.value?.main?.temp ?: "Unknown Temperature"}°C, ${
                                    currentWeather.value?.weather?.get(
                                        0
                                    )?.description ?: "Unknown Weather"
                                }"
                            )
                            .putString("location", location)
                            .build()

                        val workRequest =
                            OneTimeWorkRequestBuilder<WeatherNotificationWorker>()
                                .setInputData(inputData)
                                .build()
                        WorkManager.getInstance(context).enqueue(workRequest)
                    }
                }
            }
        }
    )
}