package com.amany.taks.models.local.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.amany.taks.models.FavoriteCity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDAOTest {

    private lateinit var favoriteDao: FavoriteDAO
    private lateinit var db: WeatherDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        favoriteDao = db.getFavoriteCityDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertFavorite() = runBlocking {
        val city = FavoriteCity(name = "Cairo", lat = 30.0444, lon = 31.2357, countryCode = "EG")
        favoriteDao.insertFavorite(city)

        val result = favoriteDao.getStoredFavoriteCities().first()

        assertEquals(1, result.size)
        assertEquals(city.name, result[0].name)
        assertEquals(city.lat, result[0].lat, 0.0001)
        assertEquals(city.lon, result[0].lon, 0.0001)
        assertEquals(city.countryCode, result[0].countryCode)
    }

    @Test
    fun testDeleteFavorite() = runBlocking {
        val city = FavoriteCity(name = "Cairo", lat = 30.0444, lon = 31.2357, countryCode = "EG")
        favoriteDao.insertFavorite(city)

        favoriteDao.deleteFavorite(city)
        val result = favoriteDao.getStoredFavoriteCities().first()

        assertEquals(0, result.size)
    }
}
