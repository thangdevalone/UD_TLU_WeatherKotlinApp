package com.example.weatherapptlu.api

import com.example.weatherapptlu.`interface`.ForecastApi
import com.example.weatherapptlu.untils.baseUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val forecastWeatherApi: ForecastApi by lazy {
        retrofit.create(ForecastApi::class.java)
    }
}
