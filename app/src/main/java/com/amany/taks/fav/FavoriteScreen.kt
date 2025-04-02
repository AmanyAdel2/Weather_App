package com.amany.taks.fav

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amany.taks.models.FavoriteCity
import com.amany.taks.models.SharedPrefs
import com.amany.taks.models.local.db.LocalState
import com.amany.taks.repository.WeatherRepository
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import android.location.Geocoder
import android.view.MotionEvent
import com.amany.taks.models.SharedCityViewModel
import java.util.Locale

@Composable
fun FavoriteScreen(weatherRepository: WeatherRepository) {
    val factory = remember { FavoriteCityViewModelFactory(weatherRepository) }
    val viewModel: FavoriteCityViewModel = viewModel(factory = factory)
    val sharedCityViewModel: SharedCityViewModel = viewModel()



    // Collect favorite cities from ViewModel
    val citiesState by viewModel.cities.collectAsState()
    val context = LocalContext.current

    var showMap by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getFavouriteCitiesFromRoom() // Load favorite cities when screen opens
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMap = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Open Map")
            }
        }
    ) { padding ->
        if (showMap) {
            OpenStreetMapScreen(weatherRepository) { showMap = false }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Favorite Cities",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    when (citiesState) {
                        is LocalState.Loading -> {
                            CircularProgressIndicator()
                        }

                        is LocalState.Success -> {
                            val cities = (citiesState as LocalState.Success).favoriteCity
                            if (cities.isEmpty()) {
                                Text("No favorite cities yet", color = Color.Gray)
                            } else {
                                Column {
                                    cities.forEach { city ->
                                        CityCard(city,
                                            onCityClick = {
                                                viewModel.fetchAndStoreWeather(city)
                                                // Set city coordinates in shared ViewModel
                                                sharedCityViewModel.setCityCoordinates(city.lat, city.lon)
                                                Toast.makeText(context, "Fetching weather for ${city.name}", Toast.LENGTH_SHORT).show()
                                            },
                                            onRemove = {
                                                viewModel.removeCityFromFavorite(city)
                                                Toast.makeText(context, "${city.name} removed", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }

                        is LocalState.Failure -> {
                            val error = (citiesState as LocalState.Failure).msg
                            Text("Error: ${error.message}", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CityCard(city: FavoriteCity, onCityClick: (FavoriteCity) -> Unit, onRemove: (FavoriteCity) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onCityClick(city) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = city.name)

            }
            IconButton(onClick = { onRemove(city) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun OpenStreetMapScreen(weatherRepository: WeatherRepository, onLocationSelected: () -> Unit) {
    val factory = remember { FavoriteCityViewModelFactory(weatherRepository) }
    val viewModel: FavoriteCityViewModel = viewModel(factory = factory) // ViewModel with factory

    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var cityName by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
                val mapView = MapView(ctx).apply {
                    setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                }

                val defaultLocation = GeoPoint(30.686414580229798, 31.57972475891442)
                mapView.controller.setZoom(10.0)
                mapView.controller.setCenter(defaultLocation)

                val marker = Marker(mapView).apply {
                    position = defaultLocation
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(marker)

                val tapOverlay = object : Overlay() {
                    override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {
                        val point = mapView?.projection?.fromPixels(e?.x?.toInt() ?: 0, e?.y?.toInt() ?: 0) as GeoPoint
                        marker.position = point
                        mapView.invalidate()

                        selectedLocation = point
                        cityName = getCityName(context, point.latitude, point.longitude).toString()
                        return true
                    }
                }
                mapView.overlays.add(tapOverlay)

                mapView
            },
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = {
                selectedLocation?.let { location ->
                    val (cityName, countryCode) = getCityName(context, location.latitude, location.longitude)

                    cityName?.let { name ->
                        countryCode?.let { country ->
                            val favoriteCity = FavoriteCity(name, location.latitude, location.longitude, country)
                            viewModel.insertCityToFavorite(favoriteCity)

                            // Save location coordinates in SharedPrefs
                            val sharedPrefs = SharedPrefs.getInstance(context)
                            sharedPrefs.setLatitude(location.latitude)
                            sharedPrefs.setLongitude(location.longitude)

                            Toast.makeText(context, "$name added to favorites!", Toast.LENGTH_SHORT).show()
                            onLocationSelected()
                        } ?: run {
                            Toast.makeText(context, "Unable to fetch country code", Toast.LENGTH_SHORT).show()
                        }
                    }
                }


            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Save to Favorites")
        }
    }
}


fun getCityName(context: Context, lat: Double, lon: Double): Pair<String?, String?> {
    val geoCoder = Geocoder(context, Locale.getDefault())
    val fullAddress = geoCoder.getFromLocation(lat, lon, 1)

    val cityName = fullAddress?.firstOrNull()?.adminArea ?: "Unknown City"
    val countryCode = fullAddress?.firstOrNull()?.countryCode ?: "Unknown Country"

    return Pair(cityName, countryCode)
}
