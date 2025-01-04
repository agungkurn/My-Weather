package id.ak.myweather.data.repository

import id.ak.myweather.data.model.CurrentWeatherDtoToEntityMapper
import id.ak.myweather.data.api.WeatherApi
import id.ak.myweather.data.local.CurrentWeatherDataStoreManager
import id.ak.myweather.data.model.CurrentWeatherEntityToDtoMapper
import id.ak.myweather.data.model.CurrentWeatherResponse
import id.ak.myweather.domain.entity.CurrentWeatherEntity
import id.ak.myweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultWeatherRepository @Inject constructor(
    private val api: WeatherApi,
    private val currentWeatherDtoToEntityMapper: CurrentWeatherDtoToEntityMapper,
    private val currentWeatherEntityToDtoMapper: CurrentWeatherEntityToDtoMapper,
    private val currentWeatherDataStoreManager: CurrentWeatherDataStoreManager
) : WeatherRepository {
    override val cachedWeather = currentWeatherDataStoreManager.cachedWeather.map { data ->
        data?.let { currentWeatherDtoToEntityMapper.map(it) }
    }
    override val hasSavedLocation = currentWeatherDataStoreManager.hasSavedLocation

    override suspend fun getCurrentWeather(
        longitude: Double,
        latitude: Double
    ): CurrentWeatherEntity {
        val result = api.getCurrentWeather(longitude, latitude)
        return currentWeatherDtoToEntityMapper.map(result)
    }

    override suspend fun cacheWeather(
        currentWeatherEntity: CurrentWeatherEntity,
        saveLocation: Boolean
    ) {
        val response = currentWeatherEntityToDtoMapper.map(currentWeatherEntity)
        currentWeatherDataStoreManager.cacheWeather(response, saveLocation)
    }
}