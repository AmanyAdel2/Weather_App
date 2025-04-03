package com.amany.taks.alarm.model.repo


import com.amany.taks.alarm.model.db.AlarmEntity
import com.amany.taks.alarm.model.db.AlarmLocalDataSource
import kotlinx.coroutines.flow.Flow

class AlarmRepository(private val localDataSource: AlarmLocalDataSource) {

    suspend fun insertAlarm(alarm: AlarmEntity) {
        localDataSource.insertAlarm(alarm)
    }

    fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return localDataSource.getAllAlarms()
    }

    suspend fun deleteAlarm(alarm: AlarmEntity) {
        localDataSource.deleteAlarm(alarm)
    }
}
