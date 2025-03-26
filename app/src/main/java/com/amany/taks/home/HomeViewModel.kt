package com.amany.taks.home

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amany.taks.models.SharedPrefs
import com.amany.taks.remote.WeatherState
import com.amany.taks.repository.WeatherRepository
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
    private val _currentWeather = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val currentWeather = _currentWeather.asStateFlow()

    fun getCurrentWeather() {
        val sharedPrefs = SharedPrefs.getInstance(context)
        val latitude = sharedPrefs.getLatitude() ?: 10.0
        val longitude = sharedPrefs.getLongitude() ?: 10.0

        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.getCurrentWeather(latitude, longitude, "metric", "en")
                .catch {
                    _currentWeather.value = WeatherState.Failure(it)
                }
                .collect {
                    _currentWeather.value = WeatherState.Success(it)
                }
        }
    }
}

class HomeScreenViewModelFactory(private val repo: WeatherRepository, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo, context) as T
    }
}
