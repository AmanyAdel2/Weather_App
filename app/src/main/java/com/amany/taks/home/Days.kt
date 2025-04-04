package com.amany.taks.home

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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun FiveDayForecast(forecastList: List<HourlyForecast>, sharedPrefs: SharedPrefs) {
    val groupedForecast = forecastList.groupBy { it.dt_txt?.split(" ")?.get(0) ?:  0} // Extract "yyyy-MM-dd"
    val dailyForecasts = groupedForecast.values.take(5) // Take first 5 unique days

    LazyRow {
        items(dailyForecasts) { dailyData ->
            DailyForecastItem(dailyData, sharedPrefs)
        }
    }
}

@Composable
fun DailyForecastItem(dailyData: List<HourlyForecast>, sharedPrefs: SharedPrefs) {
    val temperatureUnit = sharedPrefs.getTemp()
    val temperatureSymbol = getTemperatureSymbol(temperatureUnit)

    val averageTemp = dailyData.map { it.main.temp }.average()
    val convertedTemperature = convertTemperature(averageTemp, temperatureUnit)

    val weatherIcon = getWeatherIcon(dailyData.first().weather.firstOrNull()?.icon)
    val date = formatDate(dailyData.first().dt)

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(120.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Image(
                painter = painterResource(id = weatherIcon),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${convertedTemperature.roundToInt()}°$temperatureSymbol",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

/** Converts temperature based on the selected unit */
fun convertTemperature(tempInKelvin: Double, unit: String): Int {
    return when (unit) {
        "Celsius" -> (tempInKelvin - 273.15).roundToInt()
        "Fahrenheit" -> ((tempInKelvin - 273.15) * 9 / 5 + 32).roundToInt()
        else -> tempInKelvin.roundToInt() // Kelvin by default
    }
}

/** Returns the correct temperature unit symbol */
fun getTemperatureSymbol(unit: String): String {
    return when (unit) {
        "Fahrenheit" -> "F"
        "Celsius" -> "C"
        else -> "K"
    }
}


// Function to format the date properly
fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

@Composable
fun ForecastItem(forecast: HourlyForecast) {
    val dateParts = forecast.dt_txt?.split(" ")?.get(0) ?: ""
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.parse(dateParts)
    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    val formattedDay = dayFormat.format(date ?: Date())

    val temperatureMin = forecast.main.temp_min.roundToInt()
    val temperatureMax = forecast.main.temp_max.roundToInt()
    val temperatureSymbol = "°C"

    val weatherIcon = getWeatherIcon(forecast.weather.firstOrNull()?.icon)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = weatherIcon),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = formattedDay, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "$temperatureMin / $temperatureMax $temperatureSymbol", fontSize = 16.sp)
            }
        }
    }
}

fun getTemperatureSymbol(sharedPrefs: SharedPrefs): String {
    return when (sharedPrefs.getTemp()) {
        "Fahrenheit" -> "°F"
        "Celsius" -> "°C"
        else -> "K"
    }
}
