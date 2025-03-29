package com.amany.taks.models.remote

import com.amany.taks.models.WeatherList

sealed class WeatherState() {
    data class Success(val weatherResponse: WeatherList) : WeatherState()
    data class Failure(val msg: Throwable) : WeatherState()
    object Loading : WeatherState()

}