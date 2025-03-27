package com.amany.taks.settings

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amany.taks.map.OpenStreetMapActivity
import com.amany.taks.models.SharedPrefs
import java.util.Locale


//class SharedPrefs private constructor(context: Context) {
//
//    private val sharedPreferences: SharedPreferences =
//        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//
//    private val _windSpeedFlow = MutableStateFlow(getWindSpeedPreference())
//    val windSpeedFlow: Flow<String> get() = _windSpeedFlow
//
//    companion object {
//        private const val PREFS_NAME = "WeatherPrefs"
//        private const val KEY_WIND_SPEED = "wind_speed"
//        private const val KEY_TEMPERATURE = "temperature"
//        private const val KEY_LANGUAGE = "language"
//        private const val KEY_LOCATION_MODE = "location_mode"
//
//        @Volatile
//        private var instance: SharedPrefs? = null
//
//        fun getInstance(context: Context): SharedPrefs {
//            return instance ?: synchronized(this) {
//                instance ?: SharedPrefs(context.applicationContext).also { instance = it }
//            }
//        }
//    }
//
//    fun getWindSpeedPreference(): String {
//        return sharedPreferences.getString(KEY_WIND_SPEED, "Meter/Sec") ?: "Meter/Sec"
//    }
//
//    fun setWindSpeedPreference(value: String) {
//        sharedPreferences.edit().putString(KEY_WIND_SPEED, value).apply()
//        _windSpeedFlow.value = value
//    }
//
//    fun getTemp(): String {
//        return sharedPreferences.getString(KEY_TEMPERATURE, "Celsius") ?: "Celsius"
//    }
//
//    fun setTemp(value: String) {
//        sharedPreferences.edit().putString(KEY_TEMPERATURE, value).apply()
//    }
//
//    fun getLanguage(): String {
//        return sharedPreferences.getString(KEY_LANGUAGE, "en") ?: "en"
//    }
//
//    fun setLanguage(value: String) {
//        sharedPreferences.edit().putString(KEY_LANGUAGE, value).apply()
//    }
//
//    fun getLocationMode(): String {
//        return sharedPreferences.getString(KEY_LOCATION_MODE, "GPS") ?: "GPS"
//    }
//
//    fun setLocationMode(value: String) {
//        sharedPreferences.edit().putString(KEY_LOCATION_MODE, value).apply()
//    }
//}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val sharedPrefs = remember { SharedPrefs.getInstance(context) }
    val scope = rememberCoroutineScope()
    var selectedMode by remember { mutableStateOf(sharedPrefs.getLocationMode()) }


    var windSpeed by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // Collect wind speed updates in real-time
    LaunchedEffect(Unit) {
        windSpeed = sharedPrefs.getWindSpeedPreference().toString()
        temperature = sharedPrefs.getTemp().toString()
        language = sharedPrefs.getLanguage().toString()
        location = sharedPrefs.getLocationMode().toString()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Settings")
        Spacer(modifier = Modifier.height(16.dp))

        // Wind Speed Settings
      WindSpeedSelection(sharedPrefs)

        Spacer(modifier = Modifier.height(16.dp))

        // Temperature Settings
//
        TemperatureSelectionScreen(sharedPrefs)

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
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Choose Location Mode", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                RadioButton(
                    selected = selectedMode == "GPS",
                    onClick = {
                        selectedMode = "GPS"
                        sharedPrefs.setLocationMode("GPS")
                        Toast.makeText(context, "GPS selected", Toast.LENGTH_SHORT).show()
                    }
                )
                Text(text = "Use GPS", modifier = Modifier.padding(start = 8.dp))
            }

            Row {
                RadioButton(
                    selected = selectedMode == "Map",
                    onClick = {
                        selectedMode = "Map"
                        sharedPrefs.setLocationMode("Map")
                        context.startActivity(Intent(context, OpenStreetMapActivity::class.java))
                    }
                )
                Text(text = "Select Location from Map", modifier = Modifier.padding(start = 8.dp))
            }
        }}
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
@Composable
fun WindSpeedSelection(sharedPrefs: SharedPrefs) {
    val context = LocalContext.current
    var windSpeed by remember { mutableStateOf(sharedPrefs.getWindSpeedPreference()) }

    Column {
        Text(text = "Wind Speed", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Row(verticalAlignment = Alignment.CenterVertically) {
            listOf("Miles/Hour", "Meter/Sec").forEach { speed ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = windSpeed == speed,
                        onClick = {
                            windSpeed = speed
                            sharedPrefs.setWindSpeedPreference(speed)
                            Toast.makeText(context, "$speed selected", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Text(text = speed, modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

fun setAppLocale(activity: Activity, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration()
    config.setLocale(locale)

    activity.baseContext.resources.updateConfiguration(
        config,
        activity.baseContext.resources.displayMetrics
    )

    // Restart activity to apply language change instantly
    activity.recreate()
}
@Composable
fun LanguageSelectionScreen(sharedPrefs: SharedPrefs, activity: Activity) {
    var selectedLanguage by remember { mutableStateOf(sharedPrefs.getLanguage()) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Language", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            listOf("ar" to "Arabic", "en" to "English").forEach { (code, label) ->
                RadioButton(
                    selected = selectedLanguage == code,
                    onClick = {
                        selectedLanguage = code
                        sharedPrefs.setLanguage(code)
                        setAppLocale(activity, code) // Change language dynamically
                        Toast.makeText(context, "$label selected", Toast.LENGTH_SHORT).show()
                    }
                )
                Text(text = label, modifier = Modifier.padding(start = 8.dp))
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}
@Composable
fun TemperatureSelectionScreen(sharedPrefs: SharedPrefs) {
    var selectedTemperature by remember { mutableStateOf(sharedPrefs.getTemp()) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Temperature Unit",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
            listOf("Kelvin", "Celsius", "Fahrenheit").forEach { temp ->
                RadioButton(
                    selected = selectedTemperature == temp,
                    onClick = {
                        selectedTemperature = temp
                        sharedPrefs.setTemp(temp)
                        Toast.makeText(context, "$temp selected", Toast.LENGTH_SHORT).show()
                    }
                )
                Text(text = temp, modifier = Modifier.padding(start = 8.dp))
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}


@Composable
fun LocationSelectionScreen(onMapSelected: () -> Unit) {
    val context = LocalContext.current
    val sharedPrefs = remember { SharedPrefs.getInstance(context) }
    var location by remember { mutableStateOf(sharedPrefs.getLocationMode()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Select Location Mode", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            listOf(
                SharedPrefs.LOCATION_GPS to "GPS",
                SharedPrefs.LOCATION_MAP to "Map"
            ).forEach { (mode, label) ->
                RadioButton(
                    selected = location == mode,
                    onClick = {
                        location = mode
                        sharedPrefs.setLocationMode(mode)
                        Toast.makeText(context, "$label selected", Toast.LENGTH_SHORT).show()

                        if (mode == SharedPrefs.LOCATION_MAP) {
                            onMapSelected()
                        }
                    }
                )
                Text(text = label, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
