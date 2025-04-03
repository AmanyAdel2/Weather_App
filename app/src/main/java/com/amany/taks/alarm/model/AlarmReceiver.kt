package com.amany.taks.alarm.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.amany.taks.R

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

        // notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable._03d)
            .setContentTitle("Taks")
            .setContentText("Check the weather, Harry!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Show notification
        notificationManager.notify(notificationId, builder.build())
    }
}