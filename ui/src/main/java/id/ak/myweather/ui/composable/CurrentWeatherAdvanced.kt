package id.ak.myweather.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.ak.myweather.ui.R
import id.ak.myweather.ui.theme.SunriseColor
import id.ak.myweather.ui.utils.formatAsString
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

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

            if (visibility >= 1000) append(" km") else append(" m")
        }
    }
    val formattedWindSpeed = remember(windSpeed) {
        formatter.format(windSpeed)
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            WeatherInfoBox(
                modifier = Modifier.width(100.dp),
                dark = night,
                title = stringResource(R.string.pressure),
                content = "$formattedPressure hPa"
            )
        }
        item {
            WeatherInfoBox(
                modifier = Modifier.width(100.dp),
                dark = night,
                title = stringResource(R.string.humidity),
                content = "$humidity%"
            )
        }
        item {
            WeatherInfoBox(
                modifier = Modifier.width(100.dp),
                dark = night,
                title = stringResource(R.string.visibility),
                content = formattedVisibility,
            )
        }
        item {
            WeatherInfoBox(
                modifier = Modifier.width(100.dp),
                dark = night,
                title = stringResource(R.string.wind),
                content = stringResource(R.string.speed_m_s, formattedWindSpeed),
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
            item {
                val formattedRain = remember(it) { formatter.format(it) }
                WeatherInfoBox(
                    modifier = Modifier.width(100.dp),
                    dark = night,
                    title = stringResource(R.string.rain),
                    content = "$formattedRain mm/h"
                )
            }
        }
    }
}

@Composable
fun SunInfographic(
    dark: Boolean,
    sunrise: Long,
    sunset: Long,
    modifier: Modifier = Modifier
) {
    val cardColor = remember(dark) { (if (dark) Color.Black else Color.White).copy(alpha = .5f) }
    val sunriseTime = remember(sunrise) { sunrise.formatAsString(pattern = "HH:mm") }
    val sunsetTime = remember(sunset) { sunset.formatAsString(pattern = "HH:mm") }

    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 8.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.sunrise),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Start
            )
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.sunset),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.End
            )
        }
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            progress = { getSunProgress(sunrise, sunset).toFloat() },
            color = SunriseColor,
            trackColor = Color.Gray,
            gapSize = (-2).dp,
            drawStopIndicator = {}
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = sunriseTime,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Start
            )
            Text(
                modifier = Modifier.weight(1f),
                text = sunsetTime,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.End
            )
        }
    }
}

private fun getSunProgress(sunrise: Long, sunset: Long, isInMillisecond: Boolean = false): Double {
    val currentTime = LocalTime.now()
    val sunriseTime = LocalTime.ofInstant(
        (if (isInMillisecond) Instant.ofEpochMilli(sunrise) else Instant.ofEpochSecond(sunrise)),
        ZoneId.systemDefault()
    )
    val sunsetTime = LocalTime.ofInstant(
        (if (isInMillisecond) Instant.ofEpochMilli(sunset) else Instant.ofEpochSecond(sunset)),
        ZoneId.systemDefault()
    )

    val totalDuration = Duration.between(sunriseTime, sunsetTime).toMinutes().toDouble()
    val elapsedDuration = Duration.between(sunriseTime, currentTime).toMinutes().toDouble()
    return when {
        currentTime.isBefore(sunriseTime) -> 0.0
        currentTime.isAfter(sunsetTime) -> 1.0
        else -> elapsedDuration / totalDuration
    }
}