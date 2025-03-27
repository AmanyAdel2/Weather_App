package com.amany.taks.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amany.taks.R
import com.amany.taks.models.SharedPrefs
import com.amany.taks.remote.RemoteDataSource
import com.amany.taks.remote.RetrofitHelper
import com.amany.taks.remote.WeatherState
import com.amany.taks.repository.WeatherRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val factory = HomeScreenViewModelFactory(
        WeatherRepository.getInstance(RemoteDataSource(RetrofitHelper.retrofitService)),
        context
    )
    val homeViewModel: HomeViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) {
        homeViewModel.getCurrentWeather()
    }

    val result by homeViewModel.currentWeather.collectAsState()

    val sharedPrefs = SharedPrefs.getInstance(LocalContext.current)
    val temperatureUnit = sharedPrefs.getTemp()
    val windSpeedUnit = sharedPrefs.getWindSpeedPreference() // "Miles/Hour" or "Meter/Sec"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Weather App",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val response = result) {
                is WeatherState.Failure -> {
                    Text(text = "Failed to load weather", color = Color.Red, fontSize = 18.sp)
                    Log.d(TAG, "Failure: $response")
                }
                WeatherState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }
                is WeatherState.Success -> {
                    val weather = response.weatherResponse
                    val temperatureSymbol = getTemperatureSymbol(temperatureUnit)
                    val convertedTemperature = convertTemperature(weather.main.temp, temperatureUnit)
                    val weatherIcon = getWeatherIcon(weather.weather.firstOrNull()?.icon)
                    val windSpeedUnit = sharedPrefs.getWindSpeedPreference() ?: "Meter/Sec"
                    val windSpeed = weather.wind.speed?.let { convertWindSpeed(it, windSpeedUnit) } ?: "N/A"



                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF292929))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = weatherIcon),
                                contentDescription = "Weather Icon",
                                modifier = Modifier.size(80.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "$convertedTemperature°$temperatureSymbol",
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercaseChar() } ?: "N/A",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Divider(color = Color.Gray, thickness = 1.dp)

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyColumn {
                                item {
                                    WeatherDetailRow("Humidity", "${weather.main.humidity?.toString() ?: "N/A"} %")
                                    WeatherDetailRow("Wind Speed", "$windSpeed $windSpeedUnit")

                                    WeatherDetailRow("Visibility", "${weather.visibility?.div(1000)?.toString() ?: "N/A"} km")
                                    WeatherDetailRow("Cloud Cover", "${weather.clouds.all?.toString() ?: "N/A"} %")
                                    WeatherDetailRow("Sunrise", weather.sys.sunrise?.let { formatTime(it) } ?: "N/A")
                                    WeatherDetailRow("Sunset", weather.sys.sunset?.let { formatTime(it) } ?: "N/A")
                                    WeatherDetailRow("City", weather.name ?: "N/A")
                                    WeatherDetailRow("Country", weather.sys.country ?: "N/A")

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.LightGray, fontSize = 16.sp)
        Text(text = value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// Function to get weather icon resource ID
fun getWeatherIcon(iconCode: String?): Int {
    return when (iconCode) {
        "01d" -> R.drawable._01d
        "02d" -> R.drawable._02d
        "03d", "04d" -> R.drawable._03d
        "09d", "10d" -> R.drawable._09n
        "11d" -> R.drawable._11d
        "13d" -> R.drawable._13d
        "50d" -> R.drawable._04n
        else -> R.drawable._02n
    }
}

// Function to format time from Unix timestamp
fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

fun convertMetersPerSecToMilesPerHour(metersPerSec: Double): String {
    val result = metersPerSec * 2.23694
    return String.format("%.2f", result)
}

// Function to get API temperature units
fun getTemperatureUnits(tempUnitPreference: String): String {
    return when (tempUnitPreference) {
        "Fahrenheit" -> "imperial"
        "Celsius" -> "metric"
        else -> ""
    }
}

// Function to get temperature symbols
fun getTemperatureSymbol(tempUnitPreference: String?): String {
    return when (tempUnitPreference) {
        "Fahrenheit" -> "F"
        "Celsius" -> "C"
        else -> "K"
    }
}

// Function to convert temperature based on selected unit
fun convertTemperature(tempInKelvin: Double, unit: String?): Double {
    return when (unit) {
        "Fahrenheit" -> (tempInKelvin - 273.15) * 9 / 5 + 32
        "Celsius" -> tempInKelvin - 273.15
        else -> tempInKelvin
    }
}
fun convertWindSpeed(speedInMetersPerSec: Double, unit: String): String {
    return if (unit == "Miles/Hour") {
        String.format("%.2f", speedInMetersPerSec * 2.23694) // Convert to mph
    } else {
        String.format("%.2f", speedInMetersPerSec) // Keep as m/s
    }
}



//@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}
