package com.amany.taks.repository

import android.util.Log
import com.amany.taks.models.FavoriteCity
import com.amany.taks.models.WeatherList
import com.amany.taks.models.local.db.WeatherDbRes
import com.amany.taks.models.local.db.WeatherLocalDataSource
import com.amany.taks.models.remote.RemoteDataSource
import com.amany.taks.models.remote.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

class WeatherRepository private constructor(
    private val remoteDataSource: RemoteDataSource, private val localSource: WeatherLocalDataSource
) {
    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String, lang: String): Flow<WeatherList> {
        return remoteDataSource.getCurrentWeatherOverNetwork(lat,lon,units,lang)
    }
    suspend fun getForecastWeather(lat: Double, lon: Double, units: String, lang: String): Flow<WeatherList> {
        return flow {
            val response = remoteDataSource.weathreService.getWeatherForecast(lat, lon, units, lang)

            Log.d("API_CALL", "Forecast API Response: ${response.body()}")

            if (response.isSuccessful) {
                val weatherList = response.body()
                if (weatherList != null) {
                    emit(weatherList)
                } else {
                    throw Exception("Forecast data is null")
                }
            } else {
                Log.e("API_ERROR", "Error: ${response.errorBody()?.string()}")
                throw HttpException(response)
            }
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getFavCitiesFromRoom(): Flow<List<FavoriteCity>> {
        return localSource.getFavCities()
    }


    suspend fun insertToFav(favoriteCity: FavoriteCity) {
        localSource.addToFav(favoriteCity)
    }

     suspend fun deleteFromFav(favoriteCity: FavoriteCity) {
        localSource.removeFromFav(favoriteCity)
    }





    fun getAllCurrentWeatherFromRoom(): Flow<WeatherDbRes> {
        return localSource.getAllStoredWeather()
    }

    suspend fun insertCurrentWeather(weatherResponse: WeatherDbRes) {
        localSource.addCurrentWeather(weatherResponse)
    }
   suspend fun deleteStoredCurrentWeather() {
        localSource.removeAllWeather()
    }






    companion object {
        private var INSTANCE: WeatherRepository? = null
        fun getInstance(
            currentWeatherRemoteRepository: RemoteDataSource,
            CurrentWeatherLocalRepository: WeatherLocalDataSource
        ): WeatherRepository {
            return INSTANCE ?: synchronized(this) {
                val temp = WeatherRepository(currentWeatherRemoteRepository, CurrentWeatherLocalRepository)
                INSTANCE = temp
                temp
            }
        }
    }

}
