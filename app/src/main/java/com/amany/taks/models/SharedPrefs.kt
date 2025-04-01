package com.amany.taks.models

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SharedPrefs internal constructor(private val context: Context) {

    private val _windSpeedFlow = MutableSharedFlow<String>()
    val windSpeedFlow = _windSpeedFlow.asSharedFlow()

    companion object {
        private const val SHARED_PREFS_NAME = "my_prefs"
        private const val KEY_CITY = "city"

        private const val KEY_WIND_SPEED = "wind_speed"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_TEMP = "temp"
        private const val KEY_LOCATION_MODE = "location_mode"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        const val LOCATION_GPS = "GPS"
        const val LOCATION_MAP = "Map"




        private const val DEFAULT_WIND_SPEED = "Meter/Sec"


        @SuppressLint("StaticFieldLeak")
        private var instance: SharedPrefs? = null

        fun getInstance(context: Context): SharedPrefs {
            return instance ?: synchronized(this) {
                instance ?: SharedPrefs(context.applicationContext).also { instance = it }
            }
        }

    }
    private val prefs : SharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFS_NAME , Context.MODE_PRIVATE)
    }


    //For Wind Speed
    fun setWindSpeedPreference(speed: String) {
        prefs.edit().putString(KEY_WIND_SPEED, speed).apply()
        _windSpeedFlow.tryEmit(speed)
    }

    fun getWindSpeedPreference(): String? {
        return prefs.getString(KEY_WIND_SPEED, DEFAULT_WIND_SPEED)
    }

    fun clearWindSpeedPreference() {
        prefs.edit().remove(KEY_WIND_SPEED).apply()
        _windSpeedFlow.tryEmit(DEFAULT_WIND_SPEED)
    }

    //For Language
    fun setLanguage(language: String) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(): String? {
        return prefs.getString(KEY_LANGUAGE, "en")
    }

    fun clearLanguage() {
        prefs.edit().remove(KEY_LANGUAGE).apply()
    }
    fun setLocation(lat: Double, lon: Double) {
        prefs.edit()
            .putString(KEY_LATITUDE, lat.toString())
            .putString(KEY_LONGITUDE, lon.toString())
            .apply()
    }



    //For Temperature
    fun setTemp(temp : String) {
        prefs.edit().putString(KEY_TEMP, temp).apply()
    }

    fun getTemp(): String? {
        return prefs.getString(KEY_TEMP, "")
    }

    fun clearTemp() {
        prefs.edit().remove(KEY_LANGUAGE).apply()
    }

    fun getLocationMode(): String = prefs.getString("location_mode", "GPS") ?: "GPS"
    fun setLocationMode(mode: String) = prefs.edit().putString("location_mode", mode).apply()

    fun getLatitude(): Double? = prefs.getString("latitude", null)?.toDoubleOrNull()
    fun setLatitude(lat: Double) = prefs.edit().putString("latitude", lat.toString()).apply()

    fun getLongitude(): Double? = prefs.getString("longitude", null)?.toDoubleOrNull()
    fun setLongitude(lon: Double) = prefs.edit().putString("longitude", lon.toString()).apply()

    fun setValue(key : String , value :String){
        prefs.edit().putString(key , value)
    }

    fun getValue(key : String ) : String?{
        return prefs.getString(key , null)
    }

    fun setValueOrNull(key : String? , value :String?){

        if (key != null && value != null){
            prefs.edit().putString(key , value).apply()
        }
    }

    fun getValueOrNull(key : String? ):String?{

        if (key != null){
            prefs.edit().putString(key , null)
        }

        return null
    }

    fun clearCityValue(){
        prefs.edit().remove(KEY_CITY).apply()
    }

}


