package com.amany.taks.alarm

import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.amany.taks.home.convertTemperature
import com.amany.taks.home.getWeatherIcon
import com.amany.taks.models.SharedPrefs
import com.amany.taks.models.local.db.WeatherLocalDataSourceImpl
import com.amany.taks.models.remote.RemoteDataSource
import com.amany.taks.models.remote.RetrofitHelper.retrofit
import com.amany.taks.models.remote.WeathreService
import com.amany.taks.repository.WeatherRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class VibratorService : Service() {
    private var vibrator: Vibrator? = null

    private val vibrationPattern = longArrayOf(0L, 1000L, 1000L) // Customize as needed

    override fun onCreate() {
        super.onCreate()
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager: VibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            // backward compatibility for Android API < 31
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification(this))

        val start = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(vibrationPattern, start))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(vibrationPattern, start)
        }

        return START_STICKY
    }

    private fun createNotification(context: Context): Notification {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Alarm Vibrator"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("200", channelName, importance)
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

        val weatherIcon = getWeatherIcon(weatherData.weather.firstOrNull()?.icon)
        val convertedTemperature = convertTemperature(weatherData.main.temp, "Celsius")

        return NotificationCompat.Builder(context, "200")
            .setContentTitle("Taks")
            .setContentText("Weather: ${weatherData.weather.firstOrNull()?.description?.capitalize()} | ${convertedTemperature}°C| ${weatherData.name}")
            .setSmallIcon(weatherIcon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        vibrator?.cancel()
        vibrator = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}