package com.amany.taks.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper{
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather?lat=30.686378920165584&lon=31.579683558101713&appid=38cf948012a6c249938f9e7c56b8f698"
    val retrofitInstance = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}