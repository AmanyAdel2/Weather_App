package com.amany.taks.repository

import android.content.Context
import android.location.Geocoder
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
import retrofit2.Response
import java.util.Locale

open class WeatherRepository private constructor(
    private val remoteDataSource: RemoteDataSource, private val localSource: WeatherLocalDataSource
) {
    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String, lang: String): Flow<WeatherList> {
        return remoteDataSource.getCurrentWeatherOverNetwork(lat,lon,units,lang)
    }

    fun getForecastWeather(lat: Double, lon: Double, units: String, lang: String): Flow<WeatherList> {
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

     fun getFavCitiesFromRoom(): Flow<List<FavoriteCity>> {
        return localSource.getFavCities()
    }

    suspend fun insertToFav(favoriteCity: FavoriteCity) {
        localSource.addToFav(favoriteCity)
    }

    suspend fun deleteFromFav(favoriteCity: FavoriteCity) {
        localSource.removeFromFav(favoriteCity)
    }

//    suspend fun fetchAndStoreWeather(lat: Double, lon: Double, units: String, lang: String) {
//        try {
//            val response = remoteDataSource.weathreService.getWeatherByCity(lat, lon, units, lang)
//            val city:FavoriteCity= FavoriteCity(response.city?.name!!,lat,lon,response.city?.country!!)
//
//           // debugging
//          Log.d("WeatherFetch", "Fetched Weather for ${city.name}: $response")
//
//            if (response != null) {
//              insertCurrentWeather(response) // Store weather data in Room
//                Log.d("WeatherRepository", "Weather for ${city.name} stored successfully!")
//            } else {
//              Log.e("WeatherRepository", "Failed to fetch weather for ${city.name}")
//           }
//        } catch (e: Exception) {
//          Log.e("WeatherRepository", "Error fetching weather: ${e.message}")
//        }
//   }

    suspend fun getWeatherByCity(lat: Double, lon: Double, units: String, lang: String): Flow<WeatherDbRes?> {
        return flow {
            try {
                val response = remoteDataSource.weathreService.getWeatherByCity(lat, lon, units, lang)
                Log.d("WeatherRepository", "Weather fetched successfully for $lat, $lon: $response")
                emit(response)
            } catch (e: HttpException) {
                Log.e("WeatherRepository", "HTTP Error fetching weather for $lat, $lon: ${e.code()} - ${e.message()}")
                emit(null)
            } catch (e: Exception) {
                Log.e("WeatherRepository", "Unexpected error fetching weather: ${e.localizedMessage}")
                emit(null)
            }
        }.flowOn(Dispatchers.IO)
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