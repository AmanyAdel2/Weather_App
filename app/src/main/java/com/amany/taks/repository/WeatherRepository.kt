package com.amany.taks.repository

import android.util.Log
import com.amany.taks.models.WeatherList
import com.amany.taks.remote.RemoteDataSource
import com.amany.taks.remote.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException

class WeatherRepository private constructor(
    private val remoteDataSource: RemoteDataSource
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
