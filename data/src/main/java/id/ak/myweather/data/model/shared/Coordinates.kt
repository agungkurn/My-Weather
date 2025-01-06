package id.ak.myweather.data.model.shared

import com.google.gson.annotations.SerializedName

data class Coordinates(
    @field:SerializedName("lon")
    val lon: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null
)