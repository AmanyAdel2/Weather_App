package com.amany.taks.fav

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amany.taks.models.FavoriteCity
import com.amany.taks.models.local.db.LocalState
import com.amany.taks.repository.WeatherRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch


class FavoriteCityViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    private val _cities = MutableStateFlow<LocalState>(LocalState.Loading)
    val cities: StateFlow<LocalState> = _cities

    init {
        getFavouriteCitiesFromRoom() // Fetch cities when ViewModel is created
    }

    fun getFavouriteCitiesFromRoom() {
        viewModelScope.launch {
            weatherRepository.getFavCitiesFromRoom()
                .catch { exception ->
                    _cities.value = LocalState.Failure(exception)
                }
                .collect { data ->
                    _cities.value = LocalState.Success(data)
                }
        }
    }
    fun fetchAndStoreWeather(city: FavoriteCity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    city.countryCode?.let { weatherRepository.getWeatherByCity(city.name, it) }
                if (response != null) {
                    weatherRepository.insertCurrentWeather(response)
                    Log.d("WeatherFetch", "Weather for ${city.name} stored successfully!")
                } else {
                    Log.e("WeatherFetch", "Failed to fetch weather for ${city.name}")
                }
            } catch (e: Exception) {
                Log.e("WeatherFetch", "Error fetching weather: ${e.message}")
            }
        }
    }





    fun insertCityToFavorite(favoriteCity: FavoriteCity) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.insertToFav(favoriteCity)
            getFavouriteCitiesFromRoom() // Refresh the list after inserting
        }
    }

    fun removeCityFromFavorite(favoriteCity: FavoriteCity) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteFromFav(favoriteCity)
            getFavouriteCitiesFromRoom() // Refresh after deletion
        }
    }
}

class FavoriteCityViewModelFactory(private val _repo: WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavoriteCityViewModel::class.java)){
            FavoriteCityViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("viewmodel class not found")
        }
    }
}