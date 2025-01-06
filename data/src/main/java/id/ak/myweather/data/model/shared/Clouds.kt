package id.ak.myweather.data.model.shared

import com.google.gson.annotations.SerializedName

data class Clouds(
    @field:SerializedName("all")
    val all: Int? = null
)