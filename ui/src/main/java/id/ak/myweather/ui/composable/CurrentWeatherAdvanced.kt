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
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val cardColor = remember(dark) { (if (dark) Color.Black else Color.White).copy(alpha = .5f) }
    val sunriseTime = remember(sunrise) {
        val time = LocalTime.ofInstant(Instant.ofEpochSecond(sunrise), ZoneId.systemDefault())
        time.format(formatter)
    }
    val sunsetTime = remember(sunset) {
        val time = LocalTime.ofInstant(Instant.ofEpochSecond(sunset), ZoneId.systemDefault())
        time.format(formatter)
    }

    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 8.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Sunrise",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Start
            )
            Text(
                modifier = Modifier.weight(1f),
                text = "Sunset",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.End
            )
        }
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            progress = {
                val now = System.currentTimeMillis() / 1000
                when {
                    now < sunrise -> 0f
                    now in sunrise..sunset -> (sunset.toFloat() - now.toFloat()) / sunset.toFloat()
                    else -> 100f
                }
            },
            color = SunriseColor,
            trackColor = Color.Gray
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