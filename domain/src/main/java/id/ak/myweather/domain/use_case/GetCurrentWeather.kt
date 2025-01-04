package id.ak.myweather.domain.use_case

import id.ak.myweather.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeather @Inject constructor(private val repository: WeatherRepository) {
    val cachedWeather = repository.cachedWeather

    suspend operator fun invoke(latitude: Double, longitude: Double, saveLocation: Boolean) {
        val newData = repository.getCurrentWeather(latitude, longitude)
        repository.cacheWeather(newData, saveLocation)
    }
}