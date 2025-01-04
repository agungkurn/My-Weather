package id.ak.myweather.domain.repository

import id.ak.myweather.domain.entity.GeocodeEntity
import kotlinx.coroutines.flow.Flow

interface GeocodingRepository {
    suspend fun getCoordinatesByName(query: String): List<GeocodeEntity>
}