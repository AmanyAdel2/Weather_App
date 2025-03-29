package com.amany.taks.models.local.db

import android.content.Context
import com.amany.taks.models.FavoriteCity
import com.amany.taks.models.remote.WeatherResponse

import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(context: Context) : WeatherLocalDataSource{

    private val favoriteDAO : FavoriteDAO by lazy {
        val db: WeatherDatabase = WeatherDatabase.getInstance(context)
        db.getFavoriteCityDao()
    }


    private val currentWeatherDAO : CurrentWeatherDAO by lazy {
        val db: WeatherDatabase = WeatherDatabase.getInstance(context)
        db.getWeatherDao()
    }

    override fun getFavCities(): Flow<List<FavoriteCity>> {
        return favoriteDAO.getStoredFavoriteCities()
    }

    override suspend fun addToFav(favoriteCity: FavoriteCity) {
        favoriteDAO.insertFavorite(favoriteCity)
    }

    override suspend fun removeFromFav(favoriteCity: FavoriteCity) {
        favoriteDAO.deleteFavorite(favoriteCity)
    }



    override fun getAllStoredWeather(): Flow<WeatherResponse> {
        return currentWeatherDAO.getStoredWeather()
    }

    override suspend fun addCurrentWeather(weatherResponse: WeatherResponse) {
        currentWeatherDAO.insertAllCurrentWeather(weatherResponse)
    }

    override suspend fun removeAllWeather() {
        currentWeatherDAO.deleteAllWeather()
    }
}