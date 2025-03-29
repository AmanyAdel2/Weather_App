package com.amany.taks.models.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amany.taks.models.remote.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCurrentWeather(weatherResponse: WeatherResponse)

    @Query("SELECT * FROM current_weather_table")
    fun getStoredWeather(): Flow<WeatherResponse>

    @Query("DELETE FROM current_weather_table")
    suspend fun deleteAllWeather()

}