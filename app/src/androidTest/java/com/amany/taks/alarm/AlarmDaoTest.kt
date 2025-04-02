package com.amany.taks.alarm

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlarmDaoTest {

    private lateinit var alarmDao: AlarmDao
    private lateinit var db: AlarmDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AlarmDatabase::class.java).build()
        alarmDao = db.alarmDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertAlarm() = runBlocking {
        val alarm = AlarmEntity(time = System.currentTimeMillis())
        alarmDao.insertAlarm(alarm)

        val result = alarmDao.getAllAlarms()
        val insertedAlarm = result.firstOrNull()

        //  debugging
        println("Inserted alarm: $insertedAlarm")

        assertEquals(1, result.size)
        assertEquals(alarm.time, insertedAlarm?.time)
    }

    @Test
    fun testDeleteAlarm() = runBlocking {
        val alarm = AlarmEntity(time = System.currentTimeMillis())
        alarmDao.insertAlarm(alarm)

        val resultAfterInsert = alarmDao.getAllAlarms()
        println("Alarms after insert: $resultAfterInsert")

        val insertedAlarm = resultAfterInsert.firstOrNull()
        insertedAlarm?.let {
            alarmDao.deleteAlarm(it)
        }

        val resultAfterDelete = alarmDao.getAllAlarms()
        println("Alarms after delete: $resultAfterDelete")

        assertEquals(emptyList<AlarmEntity>(), resultAfterDelete)
    }
}