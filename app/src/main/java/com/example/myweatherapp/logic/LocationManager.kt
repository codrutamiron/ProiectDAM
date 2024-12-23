package com.example.myweatherapp.logic

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await

class LocationManager(private val context: Context) {
    companion object {
        const val PERMISSION_REQUEST_CODE = 101
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Location? {
        requestLocationPermission(context as Activity, PERMISSION_REQUEST_CODE)


        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return try {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        return null
    }
}



fun requestLocationPermission(activity: Activity, requestCode: Int = 101) {
    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Request the ACCESS_FINE_LOCATION permission
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            requestCode
        )
    }
}