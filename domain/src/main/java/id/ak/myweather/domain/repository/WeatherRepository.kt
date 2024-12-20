package id.ak.myweather.domain.repository

import id.ak.myweather.domain.entity.CurrentWeatherEntity

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeatherEntity
}
