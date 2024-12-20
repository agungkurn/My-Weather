package id.ak.myweather.data.repository

import id.ak.myweather.data.model.CurrentWeatherDtoToEntityMapper
import id.ak.myweather.data.api.WeatherApi
import id.ak.myweather.domain.entity.CurrentWeatherEntity
import id.ak.myweather.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultWeatherRepository @Inject constructor(
    private val api: WeatherApi,
    private val mapper: CurrentWeatherDtoToEntityMapper
) : WeatherRepository {
    override suspend fun getCurrentWeather(
        longitude: Double,
        latitude: Double
    ): CurrentWeatherEntity {
        val result = api.getCurrentWeather(longitude, latitude)
        return mapper.map(result)
    }
}