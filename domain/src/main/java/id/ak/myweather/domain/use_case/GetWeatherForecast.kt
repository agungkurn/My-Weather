package id.ak.myweather.domain.use_case

import id.ak.myweather.domain.entity.WeatherEntity
import id.ak.myweather.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherForecast @Inject constructor(private val repository: WeatherRepository) {
    suspend operator fun invoke(latitude: Double, longitude: Double): List<WeatherEntity> {
        return repository.getWeatherForecast(latitude = latitude, longitude = longitude)
    }
}