package com.amany.taks.alarm.model

import android.content.Context
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.app.NotificationChannel
import android.app.NotificationManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.amany.taks.R
import com.amany.taks.models.SharedPrefs
import com.amany.taks.models.local.db.WeatherLocalDataSourceImpl
import com.amany.taks.models.remote.RemoteDataSource
import com.amany.taks.models.remote.RetrofitHelper.retrofit
import com.amany.taks.models.remote.WeathreService
import com.amany.taks.repository.WeatherRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AlarmWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val alarmId = inputData.getInt("ALARM_ID", 0)
        showNotification(applicationContext, alarmId)
        return Result.success()
    }

    private fun showNotification(context: Context, alarmId: Int) {
        val channelId = "alarm_channel"
        val notificationId = alarmId // Use alarm ID to avoid overwriting notifications

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val soundUri: Uri = Uri.parse("android.resource://${context.packageName}/raw/alarm_sound")

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

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable._03d)
            .setContentTitle("Taks")
            .setContentText("Weather: ${weatherData.weather.firstOrNull()?.description?.capitalize()} | ${weatherData.main.temp}°C| ${weatherData.name}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}