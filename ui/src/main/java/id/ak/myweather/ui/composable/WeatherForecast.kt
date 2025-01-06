package id.ak.myweather.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import id.ak.myweather.domain.entity.WeatherEntity
import id.ak.myweather.ui.R
import id.ak.myweather.ui.utils.formatAsString
import id.ak.myweather.ui.utils.getMiddleItem
import id.ak.myweather.ui.utils.isInSameDayAs
import id.ak.myweather.ui.utils.isToday

@Composable
fun WeatherForecast(items: List<WeatherEntity>, modifier: Modifier = Modifier) {
    val dates by remember(items) {
        derivedStateOf {
            items.filterNot { it.timestamp.isToday() }
                .map { it.timestamp.formatAsString("dd MMM") to it.timestamp }
                .distinctBy { it.first }
        }
    }
    val todayForecast by remember(items) {
        derivedStateOf { items.filter { it.timestamp.isToday() } }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stringResource(R.string.forecast_today),
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(todayForecast) {
                WeatherItem(weatherEntity = it, showTime = true)
            }
        }
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stringResource(R.string.forecast_5_days),
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(dates.size) { i ->
                val forecasts by remember(dates, items, i) {
                    derivedStateOf {
                        items.filter { it.timestamp.isInSameDayAs(dates[i].second) }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    WeatherDate(date = dates[i].first)
                    WeatherItem(weatherEntity = forecasts.getMiddleItem(), showTime = false)
                }
            }
        }
    }
}

@Composable
fun WeatherDate(date: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = date, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun WeatherItem(
    weatherEntity: WeatherEntity,
    showTime: Boolean,
    modifier: Modifier = Modifier
) {
    val time = remember(weatherEntity) {
        weatherEntity.timestamp.formatAsString("HH:mm")
    }
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showTime) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            AsyncImage(
                modifier = Modifier.size(64.dp),
                model = weatherEntity.weatherIcon,
                contentDescription = weatherEntity.weatherName
            )
            Text(
                text = "${weatherEntity.temperature} ${stringResource(R.string.celcius)}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}