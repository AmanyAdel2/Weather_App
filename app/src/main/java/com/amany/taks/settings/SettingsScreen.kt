package com.amany.taks.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale



@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val sharedPrefs = remember { SharedPrefs.getInstance(context) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var selectedMode by remember { mutableStateOf(sharedPrefs.getLocationMode()) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var hasPermission by remember { mutableStateOf(false) }
    var windSpeed by remember { mutableStateOf(sharedPrefs.getWindSpeedPreference()) }
    var selectedTemperature by remember { mutableStateOf(sharedPrefs.getTemp()) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(hasPermission, selectedMode) {
        if (hasPermission && selectedMode == "GPS") {
            requestLocationUpdates(fusedLocationClient) { lat, lon ->
                latitude = lat
                longitude = lon
                if (lat != null && lon != null) {
                    sharedPrefs.setLocation(lat, lon)
                    // Toast.makeText(context, "Location: $lat, $lon", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Settings", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Location Mode Selection
        Text(text = "Choose Location Mode", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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

        // Wind Speed Selection
        Text(text = "Wind Speed Unit", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Row {
            listOf("Miles/Hour", "Meter/Sec").forEach { speed ->
                Row {
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

        // Temperature Selection
        Text(text = "Temperature Unit", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Row {
            listOf("Kelvin", "Celsius", "Fahrenheit").forEach { temp ->
                Row {
                    RadioButton(
                        selected = selectedTemperature == temp,
                        onClick = {
                            selectedTemperature = temp
                            sharedPrefs.setTemp(temp)
                            Toast.makeText(context, "$temp selected", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Text(text = temp, modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
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
                        //  Toast.makeText(context, "$label selected", Toast.LENGTH_SHORT).show()

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
@SuppressLint("MissingPermission")
fun requestLocationUpdates(
    fusedLocationProviderClient: FusedLocationProviderClient,
    onLocationReceived: (Double?, Double?) -> Unit
) {
    val locationRequest = LocationRequest.create().apply {
        priority = Priority.PRIORITY_HIGH_ACCURACY
        interval = 5000  // Update every 5 seconds
        fastestInterval = 2000  // Fastest possible update interval
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                onLocationReceived(location.latitude, location.longitude)
            }
        }
    }

    fusedLocationProviderClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}

