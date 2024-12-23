package com.example.myweatherapp.logic

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val API_KEY = "e645407cd1d6b3b4c47a608a641096fc"

    private val weatherService: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

    private val geocodingService: GeocodingService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/geo/1.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingService::class.java)
    }

    suspend fun getCurrentWeather(location: String): WeatherResponse {
        return weatherService.getCurrentWeather(location, API_KEY)
    }

    suspend fun get5DayForecast(location: String): ForecastResponse {
        return weatherService.get5DayForecast(location, API_KEY)
    }

    suspend fun getCityName(lat: Double, lon: Double): GeocodingResponse {
        return geocodingService.getCityName(lat, lon, API_KEY).firstOrNull()
            ?: GeocodingResponse("", "")
    }
}