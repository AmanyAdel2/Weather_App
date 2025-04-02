package com.amany.taks.models

import androidx.room.Entity
import androidx.room.PrimaryKey


data class WeatherList(

    var id:Int,
    val coord: Coord,
    var main: Main,
    var sys: Sys,
    var wind: Wind,
    var weather: List<Weather>,
    var clouds: Clouds,
    var base:String,
    var visibility:Long,
    var dt:Long,
    var timezone:Double,

    var name:String,
    var cod:Long,
    val dt_txt: String?
    ,var list: List<HourlyForecast> = emptyList(),
    var city: City? = null

) {

}
data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>
)
data class AlarmData(
    val id: Int,
    var time: Long
)
data class City(
    val name: String,
    val country: String
)
data class HourlyForecast(
    val dt: Long,
    val dt_txt: String?, // ✅ Ensure this is present
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Long?
)