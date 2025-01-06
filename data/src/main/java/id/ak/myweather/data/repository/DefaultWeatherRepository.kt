package id.ak.myweather.data.repository

import id.ak.myweather.data.api.WeatherApi
import id.ak.myweather.data.local.CurrentWeatherDataStoreManager
import id.ak.myweather.data.model.CurrentWeatherDtoToEntityMapper
import id.ak.myweather.data.model.CurrentWeatherEntityToDtoMapper
import id.ak.myweather.data.model.WeatherForecastDtoToEntityMapper
import id.ak.myweather.domain.entity.WeatherEntity
import id.ak.myweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultWeatherRepository @Inject constructor(
    private val api: WeatherApi,
    private val currentWeatherDtoToEntityMapper: CurrentWeatherDtoToEntityMapper,
    private val currentWeatherEntityToDtoMapper: CurrentWeatherEntityToDtoMapper,
    private val weatherForecastDtoToEntityMapper: WeatherForecastDtoToEntityMapper,
    private val currentWeatherDataStoreManager: CurrentWeatherDataStoreManager
) : WeatherRepository {
    override val cachedWeather = currentWeatherDataStoreManager.cachedWeather.map { data ->
        data?.let { currentWeatherDtoToEntityMapper.map(it) }
    }
    override val hasSavedLocation = currentWeatherDataStoreManager.hasSavedLocation

    override suspend fun getCurrentWeather(
        longitude: Double,
        latitude: Double
    ): WeatherEntity {
        val result = api.getCurrentWeather(longitude, latitude)
        return currentWeatherDtoToEntityMapper.map(result)
    }

    override suspend fun cacheWeather(
        weatherEntity: WeatherEntity,
        saveLocation: Boolean
    ) {
        val response = currentWeatherEntityToDtoMapper.map(weatherEntity)
        currentWeatherDataStoreManager.cacheWeather(response, saveLocation)
    }

    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double
    ): List<WeatherEntity> {
        val result = api.getWeatherForecast(latitude = latitude, longitude = longitude)
        return weatherForecastDtoToEntityMapper.map(result)
    }
}