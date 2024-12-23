package com.example.myweatherapp.logic

// Current Weather Response (from current weather API)
data class Main(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int,
    val pressure: Int,
    val feels_like: Double
)

data class WeatherCondition(
    val description: String,
    val icon: String
)

data class WeatherResponse(
    val main: Main,
    val weather: List<WeatherCondition>,
    val name: String,
    val sys: Sys,
    val dt: Long
)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

// Forecast Data (from 5-day forecast API)
data class ForecastResponse(
    val list: List<WeatherData>,
    val city: City
)

data class WeatherData(
    val dt: Long,
    val main: Main,
    val weather: List<WeatherCondition>,
    val wind: Wind,
    val clouds: Clouds,
    val dt_txt: String
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Clouds(
    val all: Int
)

data class City(
    val name: String,
    val country: String
)
