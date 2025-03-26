package com.amany.taks.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver


@Composable
fun OpenStreetMapView(
    context: Context = LocalContext.current,
    onLocationSelected: (Double, Double) -> Unit
) {
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }
    }

    // Set up a marker for user-selected location
    val marker = remember {
        Marker(mapView).apply {
            setOnMarkerClickListener { marker, _ ->
                onLocationSelected(marker.position.latitude, marker.position.longitude)
                true
            }
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize()
    ) { map ->
        map.controller.setZoom(6.0)
        map.overlays.add(marker)

        // Handle user clicks to select location
        map.setOnTouchListener { _, event ->
            val geoPoint = map.projection.fromPixels(event.x.toInt(), event.y.toInt())
            marker.position = geoPoint as GeoPoint?
            map.invalidate()
            true
        }
    }
}
