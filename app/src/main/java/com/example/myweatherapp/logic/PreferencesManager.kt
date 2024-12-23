package com.example.myweatherapp.logic

import android.content.Context
import android.location.Geocoder
import java.util.Locale

class PreferencesManager(private val context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

    fun setLocation(location: String) {
        sharedPreferences.edit().putString("selected_location", location).apply()
    }

    suspend fun getLocation(): String {
        val location = sharedPreferences.getString("selected_location", "") ?: ""

        if (location.isEmpty()) {
            val geocoder = Geocoder(context, Locale.getDefault())
            LocationManager(context).getCurrentLocation().let { loc ->
                val addresses = geocoder.getFromLocation(
                    loc?.latitude ?: 0.0,
                    loc?.longitude ?: 0.0,
                    1
                )
                if (addresses?.isNotEmpty() == true) {
                    val foundLocation = addresses[0].locality
                    setLocation(foundLocation)
                    return foundLocation
                }
            }
        }

        return location
    }


}