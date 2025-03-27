package com.amany.taks.remote

import com.amany.taks.models.City
import com.amany.taks.models.HourlyForecast
import com.amany.taks.models.WeatherList

class WeatherResponse (
     var curWeather:List<WeatherList>,
      var  list: List<HourlyForecast>,
    var city: City

)