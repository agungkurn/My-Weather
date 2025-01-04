package id.ak.myweather.data.repository

import id.ak.myweather.data.api.GeocodeApi
import id.ak.myweather.data.model.GeocodeDtoToEntityMapper
import id.ak.myweather.domain.entity.GeocodeEntity
import id.ak.myweather.domain.repository.GeocodingRepository
import javax.inject.Inject

class DefaultGeocodingRepository @Inject constructor(
    private val api: GeocodeApi,
    private val geocodeDtoToEntityMapper: GeocodeDtoToEntityMapper
) : GeocodingRepository {
    override suspend fun getCoordinatesByName(query: String): List<GeocodeEntity> {
        val result = api.getCoordinatesByName(query)
        return geocodeDtoToEntityMapper.map(result)
    }
}