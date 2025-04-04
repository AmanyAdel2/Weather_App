package com.amany.taks.fav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amany.taks.models.FavoriteCity
import com.amany.taks.models.local.db.LocalState
import com.amany.taks.models.remote.WeatherResponse
import com.amany.taks.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoriteCityViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    private val _cities = MutableStateFlow<LocalState>(LocalState.Loading)
    val cities: StateFlow<LocalState> = _cities

    private val _weather = MutableStateFlow<LocalState>(LocalState.Loading)
    val weather: StateFlow<LocalState> = _weather

    init {
        getFavouriteCitiesFromRoom()
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

    fun fetchWeatherForCity(city: FavoriteCity) {
        viewModelScope.launch {
            weatherRepository.getWeatherByCity(city.lat, city.lon, "metric", "en")
                ?.catch { exception ->
                    _weather.value = LocalState.Failure(exception)
                }
                ?.collect { data ->
                    if (data == null) {
                        _weather.value = LocalState.Failure(Exception("Weather data not found for ${city.name}"))
                    } else {
                        _weather.value = LocalState.Success(data)
                    }
                }
        }
    }


    fun insertCityToFavorite(favoriteCity: FavoriteCity) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.insertToFav(favoriteCity)
            getFavouriteCitiesFromRoom()
        }
    }

    fun removeCityFromFavorite(favoriteCity: FavoriteCity) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteFromFav(favoriteCity)
            getFavouriteCitiesFromRoom()
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