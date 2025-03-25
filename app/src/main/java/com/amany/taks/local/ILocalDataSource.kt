//package com.amany.taks.local
//import kotlinx.coroutines.flow.Flow
//
//
//interface ILocalDataSource {
//    fun getFavourites(): Flow<MutableList<FavouriteCity>>
//
//    fun getAlerts(): Flow<MutableList<Alert>>
//
//    fun getCurrentWeatherData(id: String, fav: Boolean): Flow<CurrentWeatherData>
//
//    suspend fun insertCurrentWeatherData(currentWeatherData: CurrentWeatherData)
//
//    suspend fun insertFavourite(favouriteCity: FavouriteCity)
//
//    suspend fun insertAlert(alert: Alert)
//
//    suspend fun deleteCurrentWeatherData(id: String)
//
//    suspend fun deleteFavourite(favouriteCity: FavouriteCity)
//
//    suspend fun deleteAlert(alert: Alert)
//}