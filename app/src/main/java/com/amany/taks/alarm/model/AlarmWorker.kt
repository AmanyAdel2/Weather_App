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

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable._03d)
            .setContentTitle("Taks")
            .setContentText("Check the weather, Harry Up!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)


        notificationManager.notify(notificationId, builder.build())
    }
}
