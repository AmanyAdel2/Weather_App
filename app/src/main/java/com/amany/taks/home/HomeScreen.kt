package com.amany.taks.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amany.taks.remote.RemoteDataSource
import com.amany.taks.remote.RetrofitHelper
import com.amany.taks.remote.WeatherState
import com.amany.taks.repository.WeatherRepository

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen() {
    val factory = HomeScreenViewModelFactory(
        WeatherRepository.getInstance(RemoteDataSource(RetrofitHelper.retrofitService))
    )
    val homeViewModel: HomeViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) {
        homeViewModel.getCurrentWeather(10.0, 10.0, "metric", "en")
    }

    val result by homeViewModel.currentWeather.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Light gray background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            tint = Color(0xFF0F9D58),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Weather App",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val response = result) {
            is WeatherState.Failure -> {
                Text(text = "Failed to load weather", color = Color.Red)
                Log.d(TAG, "Failure: ${response}")
            }
            WeatherState.Loading -> {
                CircularProgressIndicator()
                Log.d(TAG, "Loading:")
            }
            is WeatherState.Success -> {
                val weather = response.weatherResponse
                Text(
                    text = "Temperature: ${weather.main.temp}°C\n" +
                            "Condition: ${weather.weather.firstOrNull()?.description ?: "N/A"
                            }",
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Humidity: ${weather.main.humidity}%")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Wind Speed: ${weather.wind.speed} m/s")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Pressure: ${weather.main.pressure} hPa")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "City: ${weather.name}")
                Log.d(TAG, "Success: ${weather}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Country: ${weather.sys.country}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Sunrise: ${weather.sys.sunrise}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Sunset: ${weather.sys.sunset}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Visibility: ${weather.visibility} m")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Clouds: ${weather.clouds.all}%")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Wind Direction: ${weather.wind.deg}°")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Wind Gust: ${weather.wind.gust} m/s")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Sea Level: ${weather.main.sea_level} hPa")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Ground Level: ${weather.main.grnd_level} hPa")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Temp Min: ${weather.main.temp_min}°C")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Temp Max: ${weather.main.temp_max}°C")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Feels Like: ${weather.main.feels_like}°C")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Timezone: ${weather.timezone}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "ID: ${weather.id}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Coord: ${weather.coord.lat}, ${weather.coord.lon}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Base: ${weather.base}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Cod: ${weather.cod}")
                Spacer(modifier = Modifier.height(16.dp))
             


            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}
