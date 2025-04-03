package com.amany.taks.alarm.model.db


import kotlinx.coroutines.flow.Flow

open class AlarmLocalDataSource(private val alarmDao: AlarmDao) {

    suspend fun insertAlarm(alarm: AlarmEntity) {
        alarmDao.insertAlarm(alarm)
    }

    fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return alarmDao.getAllAlarmsFlow()
    }

    suspend fun deleteAlarm(alarm: AlarmEntity) {
        alarmDao.deleteAlarm(alarm)
    }
}
