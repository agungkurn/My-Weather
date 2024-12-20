package id.ak.myweather.ui.utils

import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Color
import id.ak.myweather.ui.R
import id.ak.myweather.ui.theme.CornflowerBlue
import id.ak.myweather.ui.theme.KingsBlue

object WeatherUtils {
    private val weatherRawRes = mapOf(
        "thunderstorm" to R.raw.thunderstorm,
        "clouds" to R.raw.windy
    )
    private val dirtyAtmosphereColor = R.raw.windy

    @RawRes
    internal fun getAnimationRes(weatherGroup: String, sunrise: Long, sunset: Long): Int? {
        return when {
            weatherGroup.equals("clear", ignoreCase = true) -> {
                if (isDay(sunrise, sunset)) R.raw.clear_day else R.raw.clear_night
            }

            weatherGroup.equals("rain", ignoreCase = true) -> {
                if (isDay(sunrise, sunset)) R.raw.rain_day else R.raw.rain_night
            }

            weatherGroup.equals("drizzle", ignoreCase = true) -> {
                if (isDay(sunrise, sunset)) R.raw.rain_day else R.raw.rain_night
            }

            weatherGroup.equals("snow", ignoreCase = true) -> {
                if (isDay(sunrise, sunset)) R.raw.snow_day else R.raw.snow_night
            }

            weatherRawRes.containsKey(weatherGroup.lowercase()) -> {
                weatherRawRes[weatherGroup.lowercase()]
            }

            else -> {
                dirtyAtmosphereColor
            }
        }
    }

    internal fun getBackgroundColor(weatherGroup: String, sunrise: Long, sunset: Long): Color {
        return if (isDay(sunrise, sunset)) {
            CornflowerBlue
        } else {
            KingsBlue
        }
    }

    private fun isDay(sunrise: Long, sunset: Long): Boolean {
        val now = System.currentTimeMillis()
        return now in sunrise..sunset
    }
}