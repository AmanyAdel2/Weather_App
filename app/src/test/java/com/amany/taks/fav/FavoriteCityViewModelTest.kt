//package com.amany.taks.fav
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.lifecycle.MutableLiveData
//import androidx.test.ext.junit.runners.AndroidJUnit4
//
//import com.amany.taks.models.FavoriteCity
//import com.amany.taks.models.local.db.LocalState
//import com.amany.taks.repository.WeatherRepository
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.runTest
//import org.junit.*
//import org.junit.runner.RunWith
//import org.junit.runners.JUnit4
//import com.amany.taks.R
//import com.amany.taks.models.remote.WeatherResponse
//import junit.framework.TestCase.assertEquals
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.test.setMain
//import org.hamcrest.MatcherAssert.assertThat
//import org.mockito.Mockito.mock
//import org.mockito.Mockito.`when`
//
//@RunWith(AndroidJUnit4::class)
//class FavoriteCityViewModelTest {
//
//    @get:Rule
//    var instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    private lateinit var weatherRepository: WeatherRepository
//    private lateinit var favoriteCityViewModel: FavoriteCityViewModel
//
//    @Before
//    fun setUp() {
//        // Mock the WeatherRepository here
//        weatherRepository = mock(WeatherRepository::class.java)
//        favoriteCityViewModel = FavoriteCityViewModel(weatherRepository)
//    }
//
//    @Test
//    fun `getFavouriteCitiesFromRoom should fetch cities from repository and update UI state`() = runTest {
//        val cities = listOf(FavoriteCity("City1", ""), FavoriteCity("City2", "Country2"))
//        `when`(weatherRepository.getFavCitiesFromRoom()).thenReturn(flowOf(cities))
//
//        favoriteCityViewModel.getFavouriteCitiesFromRoom()
//
//        assertThat(favoriteCityViewModel.cities.value, isInstanceOf(LocalState.Success::class.java))
//        assertEquals(cities, (favoriteCityViewModel.cities.value as LocalState.Success).data)
//    }
//
//    @Test
//    fun `fetchWeatherForCity should update weather state with success`() = runTest {
//        val city = FavoriteCity("City1", "Country1")
//        val weatherResponse = WeatherResponse(/* mock response */)
//        `when`(weatherRepository.getWeatherByCity(city.name, city.countryCode!!)).thenReturn(flowOf(weatherResponse))
//
//        favoriteCityViewModel.fetchWeatherForCity(city)
//
//        assertThat(favoriteCityViewModel.weather.value, isInstanceOf(LocalState.Success::class.java))
//        assertEquals(weatherResponse, (favoriteCityViewModel.weather.value as LocalState.Success).data)
//    }
//
//    @Test
//    fun `fetchWeatherForCity should update weather state with failure on error`() = runTest {
//        val city = FavoriteCity("City1", "Country1")
//        `when`(weatherRepository.getWeatherByCity(city.name, city.countryCode!!)).thenReturn(flow { throw Exception("Weather not found") })
//
//        favoriteCityViewModel.fetchWeatherForCity(city)
//
//        assertThat(favoriteCityViewModel.weather.value, isInstanceOf(LocalState.Failure::class.java))
//    }
//
//    @Test
//    fun `insertCityToFavorite should add city to favorites and refresh list`() = runTest {
//        val city = FavoriteCity("City1", "Country1")
//        `when`(weatherRepository.insertToFav(city)).thenReturn(Unit)
//        `when`(weatherRepository.getFavCitiesFromRoom()).thenReturn(flowOf(listOf(city)))
//
//        favoriteCityViewModel.insertCityToFavorite(city)
//
//        // Check if the favorite city was added and the list was updated
//        assertThat(favoriteCityViewModel.cities.value, isInstanceOf(LocalState.Success::class.java))
//        assertEquals(listOf(city), (favoriteCityViewModel.cities.value as LocalState.Success).data)
//    }
//
//    @Test
//    fun `removeCityFromFavorite should remove city from favorites and refresh list`() = runTest {
//        val city = FavoriteCity("City1", "Country1")
//        `when`(weatherRepository.deleteFromFav(city)).thenReturn(Unit)
//        `when`(weatherRepository.getFavCitiesFromRoom()).thenReturn(flowOf(emptyList()))
//
//        favoriteCityViewModel.removeCityFromFavorite(city)
//
//        // Check if the favorite city was removed and the list was updated
//        assertThat(favoriteCityViewModel.cities.value, isInstanceOf(LocalState.Success::class.java))
//        assertEquals(emptyList<FavoriteCity>(), (favoriteCityViewModel.cities.value as LocalState.Success).data)
//    }
//}
