package com.amany.taks.models.remote

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amany.taks.models.City
import com.amany.taks.models.HourlyForecast
import com.amany.taks.models.WeatherList

@Entity(tableName = "current_weather_table")
class WeatherResponse (
     var curWeather:List<WeatherList>,
      var  list: List<HourlyForecast>,
     @PrimaryKey
    var city: City

)