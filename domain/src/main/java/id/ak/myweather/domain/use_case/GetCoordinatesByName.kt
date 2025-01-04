package id.ak.myweather.domain.use_case

import id.ak.myweather.domain.entity.GeocodeEntity
import id.ak.myweather.domain.repository.GeocodingRepository
import javax.inject.Inject

class GetCoordinatesByName @Inject constructor(private val repository: GeocodingRepository) {
    suspend operator fun invoke(name: String): List<GeocodeEntity> {
        return repository.getCoordinatesByName(name)
    }
}