package id.ak.myweather.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.NumberFormat

@Composable
fun CurrentWeatherAdvanced(
    modifier: Modifier = Modifier,
    night: Boolean,
    pressure: Int,
    humidity: Int,
    visibility: Int,
    windDegree: Int,
    windSpeed: Double,
    rain: Double?
) {
    val formatter = remember {
        NumberFormat.getInstance().apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
            isGroupingUsed = true
        }
    }
    val formattedPressure = remember(pressure) {
        formatter.format(pressure)
    }
    val formattedVisibility = remember(visibility) {
        val number =
            if (visibility > 1000) visibility.toDouble() / 1000.0 else visibility.toDouble()

        buildString {
            append(formatter.format(number))

            if (visibility>=1000) append(" km") else append(" m")
        }
    }
    val formattedWindSpeed = remember(windSpeed) {
        formatter.format(windSpeed)
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherInfoBox(
                dark = night,
                title = "Pressure",
                content = "$formattedPressure hPa",
                modifier = Modifier.weight(1f)
            )
            WeatherInfoBox(
                dark = night,
                title = "Humidity",
                content = "$humidity%",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherInfoBox(
                dark = night,
                title = "Visibility",
                content = formattedVisibility,
                modifier = Modifier.weight(1f)
            )
            WeatherInfoBox(
                dark = night,
                title = "Wind",
                content = "$formattedWindSpeed m/s",
                modifier = Modifier.weight(1f),
                icon = {
                    val tint = remember(night) {
                        if (night) Color.White else Color.Black
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowForward,
                        tint = tint,
                        contentDescription = "wind",
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(-90f)
                            .rotate(windDegree.toFloat())
                    )
                }
            )
        }
        rain?.let {
            val formattedRain = remember(it) { formatter.format(it) }

            WeatherInfoBox(
                dark = night,
                title = "Rain",
                content = "$formattedRain mm/h",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}