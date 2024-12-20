package id.ak.myweather.domain.entity

data class CurrentWeatherEntity(
    val weatherName: String,
    val weatherIcon: String,
    val weatherDescription: String,
    val temperature: Int,
    val minTemperature: Int,
    val maxTemperature: Int,
    val feelsLike: Int,
    val pressure: Int,
    val groundLevelPressure: Int,
    val seaLevelPressure: Int,
    val humidity: Int,
    val rainPrecipitation: Double?,
    val visibility: Int,
    val cloudiness: Int,
    val sunrise: Long,
    val sunset: Long,
    val locationName: String,
    val windDegrees: Int,
    val windSpeed: Double,
    val windGust: Double
)
