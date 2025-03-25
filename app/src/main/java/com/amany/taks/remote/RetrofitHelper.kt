package com.amany.taks.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper{
    const val BASE_URL = "https://api.openweathermap.org/"
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val retrofitService = retrofit.create(WeathreService::class.java)


}