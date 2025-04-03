package com.amany.taks.alarm.model.repo

import com.amany.taks.alarm.model.db.AlarmEntity
import com.amany.taks.alarm.model.db.AlarmLocalDataSource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import kotlinx.coroutines.flow.flowOf

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AlarmRepositoryTest {

    private lateinit var repository: AlarmRepository

    @Mock
    private lateinit var localDataSource: AlarmLocalDataSource

    @Before
    fun setUp() {
        repository = AlarmRepository(localDataSource)
    }

    @Test
    fun insertAlarm() = runBlocking {
        val alarm = AlarmEntity(id = 1, time = 1622575200000L) // Use a long timestamp
        `when`(localDataSource.insertAlarm(alarm)).thenReturn(Unit)

        repository.insertAlarm(alarm)

        verify(localDataSource).insertAlarm(alarm) // Verifying the interaction
    }

    @Test
    fun getAllAlarms() = runBlocking {
        val alarmList = listOf(AlarmEntity(1, 1622575200000L)) // Use a long timestamp
        `when`(localDataSource.getAllAlarms()).thenReturn(flowOf(alarmList))

        val result = repository.getAllAlarms()

        result.collect { alarms ->
            assertEquals(alarmList, alarms) // Assert that the result is equal to expected alarms list
        }
    }

    @Test
    fun deleteAlarm() = runBlocking {
        val alarm = AlarmEntity(id = 1, time = 1622575200000L) // Use a long timestamp
        `when`(localDataSource.deleteAlarm(alarm)).thenReturn(Unit)

        repository.deleteAlarm(alarm)

        verify(localDataSource).deleteAlarm(alarm) // Verifying the interaction
    }
}
