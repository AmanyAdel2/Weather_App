package com.amany.taks.repository

import com.amany.taks.models.WeatherList
import com.amany.taks.remote.RemoteDataSource
import com.amany.taks.remote.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WeatherRepository private constructor(
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String, lang: String): Flow<WeatherList> {
        return remoteDataSource.getCurrentWeatherOverNetwork(lat,lon,units,lang)
    }
    companion object {
        private var INSTANCE: WeatherRepository? = null
        fun getInstance(
            currentWeatherRemoteRepository: RemoteDataSource
        ): WeatherRepository {
            return INSTANCE ?: synchronized(this) {
                val temp = WeatherRepository(currentWeatherRemoteRepository)
                INSTANCE = temp
                temp
            }
        }
    }

}
