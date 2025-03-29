package com.amany.taks.models.local.db

import com.amany.taks.models.FavoriteCity
import com.amany.taks.models.remote.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {

    //For Favorite
    fun getFavCities(): Flow<List<FavoriteCity>>
    suspend fun addToFav(favoriteCity: FavoriteCity)
    suspend fun removeFromFav(favoriteCity: FavoriteCity)

    //For stored weather
    fun getAllStoredWeather(): Flow<WeatherResponse>
    suspend fun addCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun removeAllWeather()

}