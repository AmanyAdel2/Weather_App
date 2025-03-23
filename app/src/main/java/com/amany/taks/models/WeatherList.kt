package com.amany.taks.models

data class WeatherList(
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
    var id:Long,
    var name:String,
    var cod:Long
) {
}