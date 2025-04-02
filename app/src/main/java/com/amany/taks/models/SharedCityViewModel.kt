package com.amany.taks.models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedCityViewModel : ViewModel() {
    private val _cityCoordinates = MutableStateFlow<Pair<Double, Double>?>(null)
    val cityCoordinates: StateFlow<Pair<Double, Double>?> = _cityCoordinates

    fun setCityCoordinates(lat: Double, lon: Double) {
        _cityCoordinates.value = Pair(lat, lon)
    }
}
