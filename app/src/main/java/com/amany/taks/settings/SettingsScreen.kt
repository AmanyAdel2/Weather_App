package com.amany.taks.settings

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SharedPrefs private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _windSpeedFlow = MutableStateFlow(getWindSpeedPreference())
    val windSpeedFlow: Flow<String> get() = _windSpeedFlow

    companion object {
        private const val PREFS_NAME = "WeatherPrefs"
        private const val KEY_WIND_SPEED = "wind_speed"
        private const val KEY_TEMPERATURE = "temperature"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_LOCATION_MODE = "location_mode"

        @Volatile
        private var instance: SharedPrefs? = null

        fun getInstance(context: Context): SharedPrefs {
            return instance ?: synchronized(this) {
                instance ?: SharedPrefs(context.applicationContext).also { instance = it }
            }
        }
    }

    fun getWindSpeedPreference(): String {
        return sharedPreferences.getString(KEY_WIND_SPEED, "Meter/Sec") ?: "Meter/Sec"
    }

    fun setWindSpeedPreference(value: String) {
        sharedPreferences.edit().putString(KEY_WIND_SPEED, value).apply()
        _windSpeedFlow.value = value
    }

    fun getTemp(): String {
        return sharedPreferences.getString(KEY_TEMPERATURE, "Celsius") ?: "Celsius"
    }

    fun setTemp(value: String) {
        sharedPreferences.edit().putString(KEY_TEMPERATURE, value).apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLanguage(value: String) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, value).apply()
    }

    fun getLocationMode(): String {
        return sharedPreferences.getString(KEY_LOCATION_MODE, "GPS") ?: "GPS"
    }

    fun setLocationMode(value: String) {
        sharedPreferences.edit().putString(KEY_LOCATION_MODE, value).apply()
    }
}


@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val sharedPrefs = remember { SharedPrefs.getInstance(context) }
    val scope = rememberCoroutineScope()

    var windSpeed by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // Collect wind speed updates in real-time
    LaunchedEffect(Unit) {
        windSpeed = sharedPrefs.getWindSpeedPreference()
        temperature = sharedPrefs.getTemp()
        language = sharedPrefs.getLanguage()
        location = sharedPrefs.getLocationMode()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Settings")
        Spacer(modifier = Modifier.height(16.dp))

        // Wind Speed Settings
        Text(text = "Wind Speed")
        Row {
            listOf("Miles/Hour", "Meter/Sec").forEach { speed ->
                RadioButton(selected = windSpeed == speed, onClick = {
                    windSpeed = speed
                    sharedPrefs.setWindSpeedPreference(speed)
                    Toast.makeText(context, "$speed selected", Toast.LENGTH_SHORT).show()
                })
                Text(text = speed)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Temperature Settings
        Text(text = "Temperature")
        Row {
            listOf("Kelvin", "Celsius", "Fahrenheit").forEach { temp ->
                RadioButton(selected = temperature == temp, onClick = {
                    temperature = temp
                    sharedPrefs.setTemp(temp)
                    Toast.makeText(context, "$temp selected", Toast.LENGTH_SHORT).show()
                })
                Text(text = temp)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Language Settings
        Text(text = "Language")
        Row {
            listOf("ar" to "Arabic", "en" to "English").forEach { (code, label) ->
                RadioButton(selected = language == code, onClick = {
                    language = code
                    sharedPrefs.setLanguage(code)
                    Toast.makeText(context, "$label selected", Toast.LENGTH_SHORT).show()
                })
                Text(text = label)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Location Settings
        Text(text = "Location")
        Row {
            listOf("GPS" to "GPS", "Map" to "Map").forEach { (mode, label) ->
                RadioButton(selected = location == mode, onClick = {
                    location = mode
                    sharedPrefs.setLocationMode(mode)
                    Toast.makeText(context, "$label selected", Toast.LENGTH_SHORT).show()
                })
                Text(text = label)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
@Composable
fun SettingsScreenn() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon on the screen
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = Color(0xFF0F9D58)
        )
        // Text on the screen
        Text(text = "Settings", color = Color.Black)
    }
}