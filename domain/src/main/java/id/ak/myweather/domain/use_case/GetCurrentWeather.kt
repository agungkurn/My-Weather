package id.ak.myweather.domain.use_case

import id.ak.myweather.domain.entity.CurrentWeatherEntity
import id.ak.myweather.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeather @Inject constructor(private val repository: WeatherRepository) {
    suspend operator fun invoke(latitude: Double, longitude: Double): CurrentWeatherEntity {
        return repository.getCurrentWeather(latitude, longitude)
    }
}