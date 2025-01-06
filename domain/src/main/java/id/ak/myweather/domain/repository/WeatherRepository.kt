package id.ak.myweather.domain.repository

import id.ak.myweather.domain.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    val cachedWeather: Flow<WeatherEntity?>
    val hasSavedLocation: Flow<Boolean>
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherEntity
    suspend fun cacheWeather(weatherEntity: WeatherEntity, saveLocation: Boolean)
    suspend fun getWeatherForecast(latitude: Double, longitude: Double): List<WeatherEntity>
}
