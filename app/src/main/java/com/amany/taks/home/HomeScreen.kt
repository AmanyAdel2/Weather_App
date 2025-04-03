package com.amany.taks.home


import android.Manifest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amany.taks.R
import com.amany.taks.models.SharedCityViewModel
import com.amany.taks.models.SharedPrefs
import com.amany.taks.models.local.db.WeatherLocalDataSourceImpl
import com.amany.taks.models.remote.RemoteDataSource
import com.amany.taks.models.remote.RetrofitHelper
import com.amany.taks.models.remote.WeatherState
import com.amany.taks.repository.WeatherRepository
import com.amany.taks.settings.requestLocationUpdates
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val factory = HomeScreenViewModelFactory(
        WeatherRepository.getInstance(RemoteDataSource(RetrofitHelper.retrofitService), WeatherLocalDataSourceImpl(context)),
        context
    )
    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val forecastState by homeViewModel.forecastWeather.collectAsState()
    val currentTime = remember { mutableStateOf(getFormattedDateTime()) }
    val sharedCityViewModel: SharedCityViewModel = viewModel()
    val cityCoordinates by sharedCityViewModel.cityCoordinates.collectAsState()

    val sharedPrefs = remember { SharedPrefs.getInstance(context) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var hasPermission by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    // Request location permission on launch
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Fetch weather based on current location if permission is granted
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            requestLocationUpdates(fusedLocationClient) { lat, lon ->
                if (lat != null && lon != null) {
                    sharedPrefs.setLocation(lat, lon)
                    val units = sharedPrefs.getTemp() ?: "metric"
                    val lang = sharedPrefs.getLanguage() ?: "en"

                    homeViewModel.getCurrentWeather(lat, lon, units, lang)
                    homeViewModel.getForecastWeather(lat, lon, units, lang)
                } else {
                    Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Continuously update the time
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = getFormattedDateTime()
            kotlinx.coroutines.delay(1000)
        }
    }

    val result by homeViewModel.currentWeather.collectAsState()
    val temperatureUnit = sharedPrefs.getTemp()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentTime.value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            when (val response = result) {
                is WeatherState.Failure -> {
                    Text("Failed to load weather", color = Color.Red, fontSize = 18.sp)
                }
                WeatherState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }
                is WeatherState.Success -> {
                    val weather = response.weatherResponse
                    val convertedTemperature = temperatureUnit?.let { convertTemperature(weather.main.temp, it) }
                    val weatherIcon = getWeatherIcon(weather.weather.firstOrNull()?.icon)

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
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
                            Text(
                                text = "$convertedTemperature ${getTemperatureSymbol(temperatureUnit)}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercaseChar() } ?: "N/A",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                            Text(text = weather.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Card {
                                WeatherDetailRow("Humidity", "${weather.main.humidity}%")
                                WeatherDetailRow("Wind Speed", "${convertWindSpeed(weather.wind.speed, temperatureUnit ?: "metric")} ${if (temperatureUnit == "imperial") "mph" else "m/s"}")
                                WeatherDetailRow("Sunrise", formatTime(weather.sys.sunrise))
                            }
                        }
                    }

                    Text("Hourly Forecast", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    LazyRow {
                        items(forecastState.let { (it as? WeatherState.Success)?.weatherResponse?.list ?: emptyList() }) { forecast ->
                            HourlyForecastItem(forecast, sharedPrefs)
                        }
                    }

                    when (forecastState) {
                        is WeatherState.Success -> {
                            val weatherData = (forecastState as WeatherState.Success).weatherResponse
                            Text("5 Days Forecast", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Card { FiveDayForecast(weatherData.list, SharedPrefs.getInstance(context)) }
                        }
                        is WeatherState.Failure -> {
                            Text(text = "Failed to load forecast", color = MaterialTheme.colorScheme.error, fontSize = 18.sp)
                        }
                        WeatherState.Loading -> {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
        Text(text = label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 16.sp)
        Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

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

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

fun convertMetersPerSecToMilesPerHour(metersPerSec: Double): String {
    val result = metersPerSec * 2.23694
    return String.format("%.2f", result)
}

fun getTemperatureUnits(tempUnitPreference: String): String {
    return when (tempUnitPreference) {
        "Fahrenheit" -> "imperial"
        "Celsius" -> "metric"
        else -> ""
    }
}

fun getTemperatureSymbol(tempUnitPreference: String?): String {
    return when (tempUnitPreference) {
        "Fahrenheit" -> "F"
        "Celsius" -> "C"
        else -> "K"
    }
}

fun convertTemperature(tempInKelvin: Double, unit: String?): Double {
    return when (unit) {
        "Fahrenheit" -> (tempInKelvin - 273.15) * 9 / 5 + 32
        "Celsius" -> tempInKelvin - 273.15
        else -> tempInKelvin
    }
}

fun convertWindSpeed(speedInMetersPerSec: Double, unit: String): String {
    return if (unit == "Miles/Hour") {
        String.format("%.2f", speedInMetersPerSec * 2.23694)
    } else {
        String.format("%.2f", speedInMetersPerSec)
    }
}

fun getFormattedDateTime(): String {
    val sdf = SimpleDateFormat("EEEE, MMM d, yyyy | hh:mm a", Locale.getDefault())
    return sdf.format(Date())
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}