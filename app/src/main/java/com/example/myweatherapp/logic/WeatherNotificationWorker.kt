package com.example.myweatherapp.logic

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myweatherapp.R

class WeatherNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Replace with actual weather-fetching logic
        val currentWeather = inputData.getString("weather") ?: "Unknown Weather"
        val location = inputData.getString("location") ?: "Unknown Location"

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "weather_channel")
            .setContentTitle("Today's Weather in $location")
            .setContentText(currentWeather)
            .setSmallIcon(R.drawable.ic_weather)
            .build()

        notificationManager.notify(1, notification)

        return Result.success()
    }
}

@SuppressLint("InlinedApi")
fun requestNotificationPermission(activity: Activity, requestCode: Int = 100) {
    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Request the POST_NOTIFICATIONS permission
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            requestCode
        )
    }
}