package com.example.weatherapptlu.`interface`


import com.example.weatherapptlu.model.Forecast.ForecastWeather
import com.example.weatherapptlu.untils.api_key
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


public interface ForecastApi {
    @GET("forecast.json?key=${api_key}&days=7&aqi=yes&alerts=no&lang=vi")
    fun getForecast(@Query("q") location: String): Call<ForecastWeather>
}