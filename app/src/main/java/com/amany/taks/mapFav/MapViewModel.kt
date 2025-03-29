package com.amany.taks.mapFav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amany.taks.models.FavoriteCity
import com.amany.taks.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel (private val weatherRepository: WeatherRepository) : ViewModel(){
//    private val _cities: MutableLiveData<List<FavoriteCity>> = MutableLiveData<List<FavoriteCity>>()
//    val products : LiveData<List<FavoriteCity>> = _cities

    fun addCityToFavorite(favoriteCity : FavoriteCity){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.insertToFav(favoriteCity)
        }
    }
}
class MapViewModelFactory(private val _repo: WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            MapViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("viewmodel class not found")
        }
    }

}