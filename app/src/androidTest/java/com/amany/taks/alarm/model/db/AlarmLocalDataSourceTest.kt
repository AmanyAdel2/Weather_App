package com.amany.taks.alarm.model.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.amany.taks.alarm.model.db.AlarmDao
import com.amany.taks.alarm.model.db.AlarmDatabase
import com.amany.taks.alarm.model.db.AlarmEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class AlarmLocalDataSourceTest {

    private lateinit var database: AlarmDatabase
    private lateinit var alarmDao: AlarmDao
    private lateinit var localDataSource: AlarmLocalDataSource

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AlarmDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        alarmDao = database.alarmDao()
        localDataSource = AlarmLocalDataSource(alarmDao)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAlarm_retrievesAlarm() = runTest {
        val alarm = AlarmEntity(time = System.currentTimeMillis())

        localDataSource.insertAlarm(alarm)

        val result = alarmDao.getAllAlarmsFlow().first()

        assertThat(result.size > 0, `is`(true))
        assertThat(result[0].time, `is`(alarm.time))
    }


    @Test
    fun deleteAlarm_removesAlarm() = runTest {
        val alarm = AlarmEntity(time = System.currentTimeMillis())
        localDataSource.insertAlarm(alarm)

        localDataSource.deleteAlarm(alarm)

        val resultAfterDelete = alarmDao.getAllAlarmsFlow().first()
        assertThat(resultAfterDelete.size, `is`(0))
    }

}
