package com.amany.taks.models.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amany.taks.models.City
import com.amany.taks.models.HourlyForecast
import com.amany.taks.models.WeatherList

@Entity(tableName = "current_weather_table")
data class WeatherDbRes (val list: List<WeatherList>,
                         @PrimaryKey
                          val  city: City,
){
}