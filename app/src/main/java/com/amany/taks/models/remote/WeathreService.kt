package com.amany.taks.models.remote

import com.amany.taks.models.WeatherList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeathreService {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("appid") appid: String = "38cf948012a6c249938f9e7c56b8f698"
    ): Response<WeatherList>
    @GET("data/2.5/forecast")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("appid") appid: String = "38cf948012a6c249938f9e7c56b8f698"
    ): Response<WeatherList>
    @GET("data/2.5/onecall")
    suspend fun getHourlyWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("appid") appid: String = "38cf948012a6c249938f9e7c56b8f698"
    ): Response<WeatherList>



}
