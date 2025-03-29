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


class FavoriteCityViewModel (private val weatherRepository: WeatherRepository) : ViewModel(){
    private val _cities: MutableStateFlow<LocalState> = MutableStateFlow<LocalState>(LocalState.Loading)
    val cities : StateFlow<LocalState> = _cities

    private val TAG = "FavoriteCityViewModel"

    fun getFavouriteCitiesFromRoom(){
        viewModelScope.launch{
            weatherRepository.getFavCitiesFromRoom()
                .catch {
                    _cities.value = LocalState.Failure(it)
                }
                .collect{
                        data -> _cities.value = LocalState.Success(data)
                }
            Log.i(TAG, "getFavouriteCitiesFromRoom: ")
        }
    }

    fun insertCityToFavorite(favoriteCity : FavoriteCity){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.insertToFav(favoriteCity)
            getFavouriteCitiesFromRoom()
        }
    }
    fun removeCityFromFavorite(favoriteCity : FavoriteCity){
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