package id.ak.myweather.domain.use_case

import id.ak.myweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HasSavedLocation @Inject constructor(repository: WeatherRepository) {
    val value = repository.hasSavedLocation
}