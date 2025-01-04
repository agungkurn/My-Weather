package id.ak.myweather.domain.repository

import id.ak.myweather.domain.entity.CurrentWeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    val cachedWeather: Flow<CurrentWeatherEntity?>
    val hasSavedLocation: Flow<Boolean>
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeatherEntity
    suspend fun cacheWeather(currentWeatherEntity: CurrentWeatherEntity,saveLocation: Boolean)
}
