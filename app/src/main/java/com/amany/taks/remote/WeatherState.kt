package com.amany.taks.remote

sealed class WeatherState() {
    data class Success(val weatherResponse: Result<WeatherResponse>) : WeatherState()
    data class Failure(val msg: Throwable) : WeatherState()
    object Loading : WeatherState()

    data class SuccessCurrent(val weatherResponse: List<WeatherResponse>) : WeatherState()
}