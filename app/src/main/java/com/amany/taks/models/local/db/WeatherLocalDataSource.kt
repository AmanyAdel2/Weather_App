package com.amany.taks.models.local.db

import com.amany.taks.models.FavoriteCity
import com.amany.taks.models.remote.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {

    fun getFavCities(): Flow<List<FavoriteCity>>
    suspend fun addToFav(favoriteCity: FavoriteCity)
    suspend fun removeFromFav(favoriteCity: FavoriteCity)

    fun getAllStoredWeather(): Flow<WeatherDbRes>
    suspend fun addCurrentWeather(weatherResponse: WeatherDbRes)
    suspend fun removeAllWeather()

}