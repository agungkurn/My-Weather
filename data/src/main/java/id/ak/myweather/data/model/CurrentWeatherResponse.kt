package id.ak.myweather.data.model

import com.google.gson.annotations.SerializedName
import id.ak.myweather.data.BuildConfig
import id.ak.myweather.domain.entity.CurrentWeatherEntity
import id.ak.myweather.domain.mapper.Mapper
import javax.inject.Inject
import kotlin.math.roundToInt

data class CurrentWeatherResponse(
    @field:SerializedName("rain")
    val rain: Rain? = null,

    @field:SerializedName("visibility")
    val visibility: Int? = null,

    @field:SerializedName("timezone")
    val timezone: Int? = null,

    @field:SerializedName("main")
    val main: Main? = null,

    @field:SerializedName("clouds")
    val clouds: Clouds? = null,

    @field:SerializedName("sys")
    val sys: Sys? = null,

    @field:SerializedName("dt")
    val dt: Int? = null,

    @field:SerializedName("coord")
    val coord: Coord? = null,

    @field:SerializedName("weather")
    val weather: List<Weather?>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("cod")
    val cod: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("base")
    val base: String? = null,

    @field:SerializedName("wind")
    val wind: Wind? = null
)

data class Weather(
    @field:SerializedName("icon")
    val icon: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("main")
    val main: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class Rain(
    @field:SerializedName("1h")
    val precipitation: Double? = null
)

data class Main(
    @field:SerializedName("temp")
    val temp: Double? = null,

    @field:SerializedName("temp_min")
    val tempMin: Double? = null,

    @field:SerializedName("grnd_level")
    val grndLevel: Int? = null,

    @field:SerializedName("humidity")
    val humidity: Int? = null,

    @field:SerializedName("pressure")
    val pressure: Int? = null,

    @field:SerializedName("sea_level")
    val seaLevel: Int? = null,

    @field:SerializedName("feels_like")
    val feelsLike: Double? = null,

    @field:SerializedName("temp_max")
    val tempMax: Double? = null
)

data class Coord(
    @field:SerializedName("lon")
    val lon: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null
)

data class Clouds(
    @field:SerializedName("all")
    val all: Int? = null
)

data class Sys(
    @field:SerializedName("country")
    val country: String? = null,

    @field:SerializedName("sunrise")
    val sunrise: Long? = null,

    @field:SerializedName("sunset")
    val sunset: Long? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("type")
    val type: Int? = null
)

data class Wind(
    @field:SerializedName("deg")
    val deg: Int? = null,

    @field:SerializedName("speed")
    val speed: Double? = null,

    @field:SerializedName("gust")
    val gust: Double? = null
)

class CurrentWeatherDtoToEntityMapper @Inject constructor() :
    Mapper<CurrentWeatherResponse, CurrentWeatherEntity>() {
    override fun map(from: CurrentWeatherResponse): CurrentWeatherEntity {
        val weather = from.weather
        val main = from.main
        val wind = from.wind

        return CurrentWeatherEntity(
            weatherName = weather?.firstOrNull()?.main.orEmpty(),
            weatherIcon = weather?.firstOrNull()?.icon?.let {
                "${BuildConfig.BASE_IMAGE_URL}${it}@2x.png"
            }.orEmpty(),
            weatherDescription = weather?.map { it?.description.orEmpty() }?.joinToString()
                .orEmpty(),
            temperature = main?.temp?.roundToInt() ?: 0,
            minTemperature = main?.tempMin?.roundToInt() ?: 0,
            maxTemperature = main?.tempMax?.roundToInt() ?: 0,
            feelsLike = main?.feelsLike?.roundToInt() ?: 0,
            pressure = main?.pressure ?: 0,
            groundLevelPressure = main?.grndLevel ?: 0,
            seaLevelPressure = main?.seaLevel ?: 0,
            humidity = main?.humidity ?: 0,
            rainPrecipitation = from.rain?.precipitation ?: .0,
            visibility = from.visibility ?: 0,
            cloudiness = from.clouds?.all ?: 0,
            sunrise = from.sys?.sunrise ?: 0,
            sunset = from.sys?.sunset ?: 0,
            windSpeed = wind?.speed ?: .0,
            windDegrees = wind?.deg ?: 0,
            windGust = wind?.gust ?: .0,
            locationName = from.name.orEmpty()
        )
    }
}