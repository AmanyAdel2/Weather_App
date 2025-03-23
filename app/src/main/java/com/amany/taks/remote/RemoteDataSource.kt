package com.amany.taks.remote

import android.content.ContentValues.TAG
import android.util.Log
import retrofit2.Response

class RemoteDataSource(private val weathreService: WeathreService) {
     suspend fun getCurrentWeatherOverNetwork(lat: Double, lon: Double, units: String ,lang:String): Response<WeatherResponse> {
        val response = weathreService.getCurrentWeather(lat=30.686378920165584, lon=31.579683558101713 , units ,lang)
        Log.i(TAG, "getCurrentWeatherOverNetwork: $response")
        return response
    }
}