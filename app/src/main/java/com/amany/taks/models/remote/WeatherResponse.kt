package com.amany.taks.models.remote

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amany.taks.models.City
import com.amany.taks.models.HourlyForecast
import com.amany.taks.models.WeatherList


class WeatherResponse (


    var city: City,
     var curWeather:List<WeatherList>,
     var  list: List<HourlyForecast>

)