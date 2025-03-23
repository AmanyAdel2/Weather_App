package com.amany.taks.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amany.taks.remote.WeatherState
import com.amany.taks.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherRepository: WeatherRepository):ViewModel(){
    private val _currentWeather: MutableStateFlow<WeatherState> = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val currentWeather : StateFlow<WeatherState> = _currentWeather

    fun getCurrentWeather(latitude: Double, longitude: Double  , units: String , lang:String) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(latitude, longitude, units ,lang)
                .catch {
                    _currentWeather.value = WeatherState.Failure(it)
                }
                .collect{
                        data ->
                    _currentWeather.value = WeatherState.Success(data)
                }
        }
    }


}
class HomeScreenViewModelFactory (private val _repo: WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return HomeViewModel(_repo) as T

    }}