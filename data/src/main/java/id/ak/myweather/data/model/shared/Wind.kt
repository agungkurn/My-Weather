package id.ak.myweather.data.model.shared

import com.google.gson.annotations.SerializedName

data class Wind(
    @field:SerializedName("deg")
    val deg: Int? = null,

    @field:SerializedName("speed")
    val speed: Double? = null,

    @field:SerializedName("gust")
    val gust: Double? = null
)