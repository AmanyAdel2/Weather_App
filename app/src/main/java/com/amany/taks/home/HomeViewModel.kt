package com.amany.taks.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amany.taks.remote.WeatherState
import com.amany.taks.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherRepository: WeatherRepository):ViewModel(){
    private val _currentWeather = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val currentWeather  = _currentWeather.asStateFlow()

    fun getCurrentWeather(latitude: Double, longitude: Double  , units: String , lang:String) {
        viewModelScope.launch (Dispatchers.IO){
            val response = weatherRepository.getCurrentWeather(latitude,longitude,units,lang)
            response
                .catch {
                    _currentWeather.value = WeatherState.Failure(it)
                }
                .collect{
                    _currentWeather.value = WeatherState.Success(it)
                }
        }
    }


}
class HomeScreenViewModelFactory (private val _repo: WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return HomeViewModel(_repo) as T

    }}