package com.amany.taks.alarm.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.amany.taks.R
import com.amany.taks.models.SharedPrefs
import com.amany.taks.models.local.db.WeatherLocalDataSourceImpl
import com.amany.taks.models.remote.RemoteDataSource
import com.amany.taks.models.remote.RetrofitHelper
import com.amany.taks.models.remote.RetrofitHelper.retrofit
import com.amany.taks.models.remote.RetrofitHelper.toString
import com.amany.taks.models.remote.WeathreService
import com.amany.taks.repository.WeatherRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            showNotification(it)
        }
    }

    private fun showNotification(context: Context) {
        val channelId = "alarm_channel"
        val notificationId = 1

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //  channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Fetch weather data
        val weatherData = runBlocking {
            val localDataSource = WeatherLocalDataSourceImpl.getInstance(context)
            val remoteDataSource = RemoteDataSource.getInstance(retrofit.create(WeathreService::class.java))
            val weatherRepository = WeatherRepository.getInstance(remoteDataSource, localDataSource)
            val sharedPrefs = SharedPrefs.getInstance(context)
            val lat = sharedPrefs.getLatitude() ?: 40.7128
            val lon = sharedPrefs.getLongitude() ?: -74.0060
            weatherRepository.getCurrentWeather(lat, lon, "metric", "en").first()
        }
        // notification

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable._03d)
            .setContentTitle("Taks")
            .setContentText("Weather: ${weatherData.weather.firstOrNull()?.description?.capitalize()} | ${weatherData.main.temp}°C| ${weatherData.name}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}