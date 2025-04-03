package com.amany.taks.alarm.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amany.taks.alarm.model.db.AlarmEntity
import com.amany.taks.alarm.model.repo.AlarmRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmViewModel(private val repository: AlarmRepository) : ViewModel() {

    val alarms = repository.getAllAlarms()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addAlarm(time: Long) {
        viewModelScope.launch {
            repository.insertAlarm(AlarmEntity(time = time))
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
        }
    }
}

// The ViewModelFactory class to instantiate AlarmViewModel
class AlarmViewModelFactory(
    private val repository: AlarmRepository
) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlarmViewModel::class.java)){
            AlarmViewModel(repository) as T
        }else{
            throw IllegalArgumentException("viewmodel class not found")
        }
    }
}
