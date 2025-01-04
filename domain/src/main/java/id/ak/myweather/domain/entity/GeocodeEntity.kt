package id.ak.myweather.domain.entity

data class GeocodeEntity(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val state: String?
)
