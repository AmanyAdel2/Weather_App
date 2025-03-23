package com.amany.taks.repository

import com.amany.taks.remote.RemoteDataSource
import com.amany.taks.remote.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WeatherRepository(private val remoteDataSource: RemoteDataSource) {
    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String, lang: String): Flow<Result<WeatherResponse>> {
        return flow {
            try {
                val response = remoteDataSource.getCurrentWeatherOverNetwork(lat, lon, units, lang)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(Result.success(it))
                    } ?: emit(Result.failure(Exception("Empty response body")))
                } else {
                    emit(Result.failure(Exception("Error: ${response.code()} - ${response.message()}")))
                }
            } catch (e: IOException) {
                emit(Result.failure(Exception("Network error: ${e.message}")))
            } catch (e: HttpException) {
                emit(Result.failure(Exception("HTTP error: ${e.message}")))
            } catch (e: Exception) {
                emit(Result.failure(Exception("Unexpected error: ${e.message}")))
            }
        }
    }
}
