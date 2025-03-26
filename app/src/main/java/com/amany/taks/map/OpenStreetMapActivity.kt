package com.amany.taks.map

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.amany.taks.models.SharedPrefs
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver

class OpenStreetMapActivity : ComponentActivity() {
    private lateinit var mapView: MapView
    private lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OSMDroid Configuration
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        // Create a MapView
        mapView = MapView(this).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }

        setContentView(mapView)

        // Default location (Cairo, Egypt)
        val defaultLocation = GeoPoint(30.686414580229798, 31.57972475891442)
        mapView.controller.setZoom(10.0)
        mapView.controller.setCenter(defaultLocation)

        // Add a marker
        marker = Marker(mapView).apply {
            position = defaultLocation
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        mapView.overlays.add(marker)

        // Handle map taps
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    // Move the marker to the tapped location
                    marker.position = it
                    mapView.invalidate()

                    // Save the selected location
                    val sharedPrefs = SharedPrefs.getInstance(this@OpenStreetMapActivity)
                    sharedPrefs.setLatitude(it.latitude)
                    sharedPrefs.setLongitude(it.longitude)

                    Toast.makeText(this@OpenStreetMapActivity, "Location saved!", Toast.LENGTH_SHORT).show()

                    // Return the selected location to the calling activity
                    val resultIntent = Intent().apply {
                        putExtra("latitude", it.latitude)
                        putExtra("longitude", it.longitude)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean = false
        })

        // Add event listener for taps
        mapView.overlays.add(mapEventsOverlay)
    }
}
