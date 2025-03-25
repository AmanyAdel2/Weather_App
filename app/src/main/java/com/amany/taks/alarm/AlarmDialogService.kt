package com.amany.taks.alarm

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AlarmDialogService : Service() {


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}