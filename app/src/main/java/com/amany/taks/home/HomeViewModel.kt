package com.amany.taks.home

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amany.taks.models.HourlyForecast
import com.amany.taks.models.SharedPrefs
import com.amany.taks.remote.WeatherState
import com.amany.taks.repository.WeatherRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherRepository: WeatherRepository, private val context: Context) : ViewModel() {

    //val currentWeather = _currentWeather.asStateFlow()
    private val _currentWeather: MutableStateFlow<WeatherState> = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val currentWeather : StateFlow<WeatherState> = _currentWeather
    private val _forecastWeather: MutableStateFlow<WeatherState> = MutableStateFlow(WeatherState.Loading)
    val forecastWeather: StateFlow<WeatherState> = _forecastWeather.asStateFlow()

    //Over Network
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
    fun getForecastWeather(latitude: Double, longitude: Double, units: String, lang: String) {
        viewModelScope.launch {
            weatherRepository.getForecastWeather(latitude, longitude, units, lang)
                .catch { error ->
                    Log.e(TAG, "Forecast API Error: ${error.message}")
                    _forecastWeather.value = WeatherState.Failure(error)
                }
                .collect { forecast ->
                    if (forecast.list.isNotEmpty()) {  // ✅ Ensure non-empty list
                        _forecastWeather.value = WeatherState.Success(forecast)
                        Log.d(TAG, "Forecast Loaded: ${forecast.list.size} items")
                    } else {
                        Log.e(TAG, "Empty Forecast List Received")
                        _forecastWeather.value = WeatherState.Failure(Exception("Empty Forecast Data"))
                    }
                }
        }
    }

}

class HomeScreenViewModelFactory(private val repo: WeatherRepository, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo, context) as T
    }
}
