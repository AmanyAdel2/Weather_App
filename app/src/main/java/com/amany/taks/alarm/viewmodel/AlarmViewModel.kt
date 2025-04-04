package com.amany.taks.alarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amany.taks.alarm.model.db.AlarmEntity
import com.amany.taks.alarm.model.repo.AlarmRepository
import com.amany.taks.models.local.db.LocalState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlarmViewModel(private val repository: AlarmRepository) : ViewModel() {

    private val _alarms = MutableStateFlow<LocalState>(LocalState.Loading)
    val alarms: StateFlow<LocalState> = _alarms

    init {
        getAllAlarms()
    }

    fun getAllAlarms() {
        viewModelScope.launch {
            repository.getAllAlarms()
                .catch { exception ->
                    _alarms.value = LocalState.Failure(exception)
                }
                .collect { data ->
                    _alarms.value = LocalState.Success(data)
                }
        }
    }

    fun addAlarm(time: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlarm(AlarmEntity(time = time))
            getAllAlarms()
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlarm(alarm)
            getAllAlarms()
        }
    }


}

class AlarmViewModelFactory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            AlarmViewModel(repository) as T
        } else {
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}
