package com.amany.taks.models.local.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.amany.taks.models.City
import com.amany.taks.models.FavoriteCity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceImplTest {

    private lateinit var database: WeatherDatabase
    private lateinit var favoriteDao: FavoriteDAO
    private lateinit var weatherDao: CurrentWeatherDAO
    private lateinit var localDataSource: WeatherLocalDataSourceImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        favoriteDao = database.getFavoriteCityDao()
        weatherDao = database.getWeatherDao()
        localDataSource = WeatherLocalDataSourceImpl(context)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun addToFav_retrievesFavCity() = runTest {
        val city = FavoriteCity(name = "Cairo", lat = 30.0444, lon = 31.2357, countryCode = "EG")
        localDataSource.addToFav(city)

        val result = localDataSource.getFavCities().first()
        assertThat(result.isNotEmpty(), `is`(true))
        assertThat(result[0], `is`(city))
    }

    @Test
    fun removeFromFav_deletesCity() = runTest {
        val city = FavoriteCity(name = "Cairo", lat = 30.0444, lon = 31.2357, countryCode = "EG")
        localDataSource.addToFav(city)
        localDataSource.removeFromFav(city)

        val result = localDataSource.getFavCities().first()
        assertThat(result.isEmpty(), `is`(true))
    }

    @Test
    fun addCurrentWeather_retrievesWeather() = runTest {
        val weatherData = WeatherDbRes(city = City(name = "Cairo", country = "Egypt"), list = emptyList())
        localDataSource.addCurrentWeather(weatherData)

        val result = localDataSource.getAllStoredWeather().first()
        assertThat(result, `is`(weatherData))
    }

    @Test
    fun removeAllWeather_clearsWeatherData() = runTest {
        val weatherData = WeatherDbRes(city = City(name = "Cairo", country = "Egypt"), list = emptyList())
        localDataSource.addCurrentWeather(weatherData)
        localDataSource.removeAllWeather()

        val result = localDataSource.getAllStoredWeather().first()
        assertThat(result.list.isEmpty(), `is`(true))
    }
}
