package id.ak.myweather.data.model

import com.google.gson.annotations.SerializedName
import id.ak.myweather.data.BuildConfig
import id.ak.myweather.data.model.shared.Clouds
import id.ak.myweather.data.model.shared.Coordinates
import id.ak.myweather.data.model.shared.Main
import id.ak.myweather.data.model.shared.Weather
import id.ak.myweather.data.model.shared.Wind
import id.ak.myweather.domain.entity.WeatherEntity
import id.ak.myweather.domain.mapper.Mapper
import javax.inject.Inject
import kotlin.math.roundToInt

data class WeatherForecastResponse(
    @field:SerializedName("list")
    val list: List<WeatherForecast>? = null
)

data class WeatherForecast(
    @field:SerializedName("dt")
    val timestamp: Long? = null,

    @field:SerializedName("pop")
    val precipitation: Double? = null,

    @field:SerializedName("visibility")
    val visibility: Int? = null,

    @field:SerializedName("weather")
    val weather: List<Weather>? = null,

    @field:SerializedName("main")
    val main: Main? = null,

    @field:SerializedName("clouds")
    val clouds: Clouds? = null,

    @field:SerializedName("wind")
    val wind: Wind? = null,

    @field:SerializedName("rain")
    val rainForecast: RainForecast? = null,

    @field:SerializedName("city")
    val city: City? = null,
)

data class RainForecast(
    @field:SerializedName("3h")
    val rain3h: Double? = null
)

data class City(
    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("sunrise")
    val sunrise: Long? = null,

    @field:SerializedName("sunset")
    val sunset: Long? = null,

    @field:SerializedName("coord")
    val coordinates: Coordinates? = null,
)

class WeatherForecastDtoToEntityMapper @Inject constructor() :
    Mapper<WeatherForecastResponse, List<WeatherEntity>>() {
    override fun map(from: WeatherForecastResponse): List<WeatherEntity> {
        return from.list?.map {
            val weather = it.weather
            val main = it.main
            val wind = it.wind

            WeatherEntity(
                weatherName = weather?.firstOrNull()?.main.orEmpty(),
                weatherIcon = weather?.firstOrNull()?.icon?.let {
                    "${BuildConfig.BASE_IMAGE_URL}${it}@2x.png"
                }.orEmpty(),
                weatherDescription = weather?.map { it.description.orEmpty() }?.joinToString()
                    .orEmpty(),
                temperature = main?.temp?.roundToInt() ?: 0,
                minTemperature = main?.tempMin?.roundToInt() ?: 0,
                maxTemperature = main?.tempMax?.roundToInt() ?: 0,
                feelsLike = main?.feelsLike?.roundToInt() ?: 0,
                pressure = main?.pressure ?: 0,
                groundLevelPressure = main?.grndLevel ?: 0,
                seaLevelPressure = main?.seaLevel ?: 0,
                humidity = main?.humidity ?: 0,
                rainPrecipitation = it.rainForecast?.rain3h ?: .0,
                visibility = it.visibility ?: 0,
                cloudiness = it.clouds?.all ?: 0,
                sunrise = it.city?.sunrise ?: 0,
                sunset = it.city?.sunset ?: 0,
                windSpeed = wind?.speed ?: .0,
                windDegrees = wind?.deg ?: 0,
                windGust = wind?.gust ?: .0,
                locationName = it.city?.name.orEmpty(),
                latitude = it.city?.coordinates?.lat ?: .0,
                longitude = it.city?.coordinates?.lon ?: .0,
                timestamp = it.timestamp ?: (System.currentTimeMillis() / 1000)
            )
        }.orEmpty()
    }
}