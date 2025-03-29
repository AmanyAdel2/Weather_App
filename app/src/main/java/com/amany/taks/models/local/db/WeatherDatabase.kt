package com.amany.taks.models.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amany.taks.models.CityConverter
import com.amany.taks.models.FavoriteCity
import com.amany.taks.models.WeatherListConverter


@Database(entities = [FavoriteCity::class , WeatherDbRes::class], version = 3 )
@TypeConverters(WeatherListConverter::class, CityConverter::class)
abstract class WeatherDatabase : RoomDatabase(){
    abstract fun getFavoriteCityDao(): FavoriteDAO

    abstract fun getWeatherDao(): CurrentWeatherDAO
    companion object{
        @Volatile
        private var INSTANCE: WeatherDatabase? = null
        fun getInstance (ctx: Context): WeatherDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext, WeatherDatabase::class.java, "weather_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance }
        }
    }
}