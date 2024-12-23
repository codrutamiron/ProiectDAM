package com.example.myweatherapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myweatherapp.logic.PreferencesManager
import com.example.myweatherapp.logic.requestLocationPermission
import com.example.myweatherapp.logic.requestNotificationPermission
import com.example.myweatherapp.ui.BottomNavigationBar
import com.example.myweatherapp.ui.screens.CalendarScreen
import com.example.myweatherapp.ui.screens.CurrentWeatherScreen
import com.example.myweatherapp.ui.screens.LocationScreen
import com.example.myweatherapp.ui.theme.MyWeatherAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel(this)

        setContent {
            MyWeatherApp()
        }
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            "weather_channel",
            "Weather Updates",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for daily weather updates."
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

    }

}

@Composable
fun MyWeatherApp() {
    MyWeatherAppTheme {
        val context = LocalContext.current
        val navController = rememberNavController()
        val locationPermissionState = remember { mutableStateOf(false) }

        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.POST_NOTIFICATIONS else ""
        ).filter { it.isNotEmpty() }.toTypedArray()

        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGranted ->
            locationPermissionState.value = isGranted.values.all { it }
        }

        // Request permission on first launch
        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionState.value = true
            } else {
                permissionLauncher.launch(permissions)
            }
        }

        // Show loader or main app content
        if (locationPermissionState.value) {
            // Main app content
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("home") { CurrentWeatherScreen() }
                    composable("calendar") { CalendarScreen() }
                    composable("search") { LocationScreen(navController) }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
