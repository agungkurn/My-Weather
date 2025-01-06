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
    val timestamp: Long? = null,

    @field:SerializedName("coord")
    val coordinates: Coordinates? = null,

    @field:SerializedName("weather")
    val weather: List<Weather>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("wind")
    val wind: Wind? = null
)

data class Rain(
    @field:SerializedName("1h")
    val precipitation: Double? = null
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

class CurrentWeatherDtoToEntityMapper @Inject constructor() :
    Mapper<CurrentWeatherResponse, WeatherEntity>() {
    override fun map(from: CurrentWeatherResponse): WeatherEntity {
        val weather = from.weather
        val main = from.main
        val wind = from.wind

        return WeatherEntity(
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
            rainPrecipitation = from.rain?.precipitation ?: .0,
            visibility = from.visibility ?: 0,
            cloudiness = from.clouds?.all ?: 0,
            sunrise = from.sys?.sunrise ?: 0,
            sunset = from.sys?.sunset ?: 0,
            windSpeed = wind?.speed ?: .0,
            windDegrees = wind?.deg ?: 0,
            windGust = wind?.gust ?: .0,
            locationName = from.name.orEmpty(),
            latitude = from.coordinates?.lat ?: .0,
            longitude = from.coordinates?.lon ?: .0,
            timestamp = from.timestamp ?: (System.currentTimeMillis() / 1000L)
        )
    }
}

class CurrentWeatherEntityToDtoMapper @Inject constructor() :
    Mapper<WeatherEntity, CurrentWeatherResponse>() {
    override fun map(from: WeatherEntity): CurrentWeatherResponse {
        return CurrentWeatherResponse(
            weather = from.weatherDescription.split(", ").map { description ->
                Weather(
                    main = from.weatherName,
                    icon = from.weatherIcon,
                    description = description
                )
            },
            main = Main(
                temp = from.temperature.toDouble(),
                tempMin = from.minTemperature.toDouble(),
                tempMax = from.maxTemperature.toDouble(),
                humidity = from.humidity,
                pressure = from.pressure,
                grndLevel = from.groundLevelPressure,
                seaLevel = from.seaLevelPressure,
                feelsLike = from.feelsLike.toDouble()
            ),
            rain = from.rainPrecipitation?.let {
                Rain(precipitation = it)
            },
            visibility = from.visibility,
            clouds = Clouds(all = from.cloudiness),
            sys = Sys(sunrise = from.sunrise, sunset = from.sunset),
            wind = Wind(deg = from.windDegrees, speed = from.windSpeed, gust = from.windGust),
            name = from.locationName,
            coordinates = Coordinates(lon = from.longitude, lat = from.latitude),
            timestamp = from.timestamp
        )
    }
}