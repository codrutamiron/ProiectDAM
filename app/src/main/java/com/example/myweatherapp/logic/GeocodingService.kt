package com.example.myweatherapp.logic

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("reverse")
    suspend fun getCityName(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): List<GeocodingResponse>
}