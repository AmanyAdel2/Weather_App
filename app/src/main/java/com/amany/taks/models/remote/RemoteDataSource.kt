package com.amany.taks.models.remote

import android.util.Log
import com.amany.taks.models.WeatherList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

class RemoteDataSource(val weathreService: WeathreService) {
    suspend fun getCurrentWeatherOverNetwork(lat: Double, lon: Double, units: String ,lang:String): Flow<WeatherList> =
        flow {
            val response = weathreService.getCurrentWeather(lat,lon, units,lang).body()
            if(response!=null){
                Log.d("API_CALL", "Current API Response: $response")

                emit(response)

            }else{
                throw Exception("No data Received")
            }
        }

    suspend fun getForecastWeatherOverNetwork(lat: Double, lon: Double, units: String, lang: String): Flow<WeatherList> {
        return flow {
            val response = weathreService.getWeatherForecast(lat, lon, units, lang)
            if (response.isSuccessful) {
                emit(response.body()!!)
            } else {
                throw HttpException(response)
            }
        }.flowOn(Dispatchers.IO)
    }
    companion object {
        @Volatile
        private var INSTANCE: RemoteDataSource? = null

        fun getInstance(weathreService: WeathreService): RemoteDataSource {
            return INSTANCE ?: synchronized(this) {
                val instance = RemoteDataSource(weathreService)
                INSTANCE = instance
                instance
            }
        }
    }



}