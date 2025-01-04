package id.ak.myweather.data.model

import com.google.gson.annotations.SerializedName
import id.ak.myweather.domain.entity.GeocodeEntity
import id.ak.myweather.domain.mapper.Mapper
import javax.inject.Inject

data class GeocodeResponse(
    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("lon")
    val lon: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null,

    @field:SerializedName("country")
    val country: String? = null,

    @field:SerializedName("state")
    val state: String? = null
)

class GeocodeDtoToEntityMapper @Inject constructor() : Mapper<GeocodeResponse, GeocodeEntity>() {
    override fun map(from: GeocodeResponse): GeocodeEntity {
        return GeocodeEntity(
            name = from.name.orEmpty(),
            longitude = from.lon ?: .0,
            latitude = from.lat ?: .0,
            country = from.country.orEmpty(),
            state = from.state
        )
    }
}