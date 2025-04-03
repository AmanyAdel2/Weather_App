package com.amany.taks.alarm.model.repo

import com.amany.taks.alarm.model.db.AlarmDao
import com.amany.taks.alarm.model.db.AlarmEntity
import com.amany.taks.alarm.model.db.AlarmLocalDataSource
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AlarmRepositoryTest {

    @Mock
    private lateinit var alarmDao: AlarmDao  // Mock the DAO instead of the data source

    private lateinit var localDataSource: AlarmLocalDataSource
    private lateinit var repository: AlarmRepository

    private val testAlarm = AlarmEntity(id = 1, time = 1623456789000)

    @Before
    fun setupRepository() {
        localDataSource = AlarmLocalDataSource(alarmDao)  // Use a real instance
        repository = AlarmRepository(localDataSource)
    }

    @Test
    fun insertAlarm_savesToLocalSource() = runBlocking {
        repository.insertAlarm(testAlarm)

        verify(alarmDao).insertAlarm(testAlarm)  // Verify on DAO, not LocalDataSource
    }

    @Test
    fun deleteAlarm_removesFromLocalSource() = runBlocking {
        repository.deleteAlarm(testAlarm)

        verify(alarmDao).deleteAlarm(testAlarm)  // Verify on DAO
    }

    @Test
    fun getAlarms_fetchesFromLocal() = runBlocking {
        `when`(alarmDao.getAllAlarmsFlow()).thenReturn(flowOf(listOf(testAlarm)))

        val result = repository.getAllAlarms().first()

        assertEquals(listOf(testAlarm), result)
    }
}
