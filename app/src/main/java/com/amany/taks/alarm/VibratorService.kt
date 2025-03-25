package com.amany.taks.alarm

import android.R
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


class VibratorService : Service() {
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager: VibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            // backward compatibility for Android API < 31,
            // VibratorManager was only added on API level 31 release.
            // noinspection deprecation
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(1, createNotification(this))

        val DELAY = 0
        val VIBRATE = 1000
        val SLEEP = 1000
        val START = 0
        val vibratePattern = longArrayOf(DELAY.toLong(), VIBRATE.toLong(), SLEEP.toLong())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(vibratePattern, START))
        } else {
            // backward compatibility for Android API < 26
            // noinspection deprecation
            vibrator?.vibrate(vibratePattern, START)
        }
        return START_STICKY

    }

    private fun createNotification(context: Context): Notification {
        // Notification Manager
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Alarm Vibrator"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("200", channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        // Build the Notification
        return NotificationCompat.Builder(context, "200")
            .setContentTitle("Alarm")
            .setContentText("Alarm is Ringing")
            .setSmallIcon(R.drawable.sym_def_app_icon) // Replace with your own icon
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