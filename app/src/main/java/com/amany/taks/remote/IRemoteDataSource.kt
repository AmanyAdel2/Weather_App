//package com.amany.taks.remote
//
//import android.content.ContentValues.TAG
//import android.util.Log
//import com.amany.taks.models.WeatherList
//import kotlinx.coroutines.flow.Flow
//import retrofit2.Response
//import kotlinx.coroutines.flow.flow
//
//class RemoteDataSource(private val weathreService: WeathreService) {
//    suspend fun getCurrentWeatherOverNetwork(lat: Double, lon: Double, units: String ,lang:String): Flow<WeatherList> =
//        flow {
//            val response = weathreService.getCurrentWeather(lat,lon, units,lang).body()
//            if(response!=null){
//                emit(response)
//            }else{
//                throw Exception("No data Received")
//            }
//        }
//}