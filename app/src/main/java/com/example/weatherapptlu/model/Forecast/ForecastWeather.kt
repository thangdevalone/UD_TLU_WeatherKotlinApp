package com.example.weatherapptlu.model.Forecast

data class ForecastWeather(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)