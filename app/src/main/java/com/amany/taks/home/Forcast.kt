package com.amany.taks.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amany.taks.models.HourlyForecast
import com.amany.taks.models.SharedPrefs
import kotlin.math.roundToInt

@Composable
fun HourlyForecastList(hourlyWeatherList: List<HourlyForecast>, sharedPrefs: SharedPrefs) {
    if (hourlyWeatherList.isEmpty()) {
        Text(text = "No hourly forecast available", color = Color.Gray, fontSize = 16.sp)
        return
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(hourlyWeatherList) { weatherItem ->
            HourlyForecastItem(weatherItem, sharedPrefs)
        }
    }
}


@Composable
fun HourlyForecastItem(weather: HourlyForecast, sharedPrefs: SharedPrefs) {
    val time = weather.dt_txt?.substring(11, 16) ?: "N/A"
    val temperature = weather.main.temp.roundToInt()
    val temperatureSymbol = getTemperatureSymbol(sharedPrefs.getTemp() ?: "metric")
    val weatherIcon = getWeatherIcon(weather.weather.firstOrNull()?.icon)

    Card(
        modifier = Modifier
            .width(90.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = time, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Image(
                painter = painterResource(id = weatherIcon),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(50.dp)
            )
            Text(text = "$temperature $temperatureSymbol", fontSize = 14.sp)
        }
    }
}

