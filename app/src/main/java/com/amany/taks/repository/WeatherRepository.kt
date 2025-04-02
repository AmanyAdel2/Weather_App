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
import java.util.Locale

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
    suspend fun fetchAndStoreWeather(city: FavoriteCity) {
        try {
            val response = remoteDataSource.weathreService.getWeatherByCity(city.name)

            // Log response for debugging
            Log.d("WeatherFetch", "Fetched Weather for ${city.name}: $response")

            if (response != null) {
                insertCurrentWeather(response) // Store weather data in Room
                Log.d("WeatherRepository", "Weather for ${city.name} stored successfully!")
            } else {
                Log.e("WeatherRepository", "Failed to fetch weather for ${city.name}")
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error fetching weather: ${e.message}")
        }
    }
    suspend fun getWeatherByCity(cityName: String, countryCode: String): WeatherDbRes? {
        return try {
            val query = "${cityName.trim()},${countryCode.trim()}"
            Log.d("WeatherRepository", "Fetching weather for: $query")

            val response = remoteDataSource.weathreService.getWeatherByCity(query)

            Log.d("WeatherRepository", "Weather fetched successfully for $query: $response")
            response
        } catch (e: HttpException) {
            Log.e("WeatherRepository", "HTTP Error fetching weather for $cityName: ${e.code()} - ${e.message()}")
            null
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Unexpected error fetching weather for $cityName: ${e.localizedMessage}")
            null
        }
    }





    fun getCityName(context: Context, lat: Double, lon: Double, onResult: (String?) -> Unit) {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lon, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<android.location.Address>) {
                    onResult(addresses.firstOrNull()?.adminArea)
                }

                override fun onError(errorMessage: String?) {
                    onResult(null)
                }
            })
        } else {
            val addressList = geocoder.getFromLocation(lat, lon, 1)
            onResult(addressList?.firstOrNull()?.adminArea)
        }
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
