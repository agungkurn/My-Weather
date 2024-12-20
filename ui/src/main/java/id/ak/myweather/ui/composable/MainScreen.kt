package id.ak.myweather.ui.composable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.rememberLottieRetrySignal
import id.ak.myweather.ui.MainViewModel
import id.ak.myweather.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val viewModel = viewModel<MainViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lottieRetrySignal = rememberLottieRetrySignal()

    val contentColor by remember {
        derivedStateOf {
            if (viewModel.backgroundColor.luminance() > .5f) Color.Black else Color.White
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = viewModel.backgroundColor,
        contentColor = contentColor
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .padding(it)
                .consumeWindowInsets(it),
            isRefreshing = uiState == UiState.Loading,
            onRefresh = { viewModel.fetchCurrentWeather() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                viewModel.weather?.let { weather ->
                    Location(locationName = weather.locationName, onClick = {})
                    viewModel.animationRes?.let {
                        CurrentWeatherSummary(
                            animationResId = it,
                            lottieRetrySignal = lottieRetrySignal,
                            temperature = weather.temperature,
                            feelsLike = weather.feelsLike,
                            maxTemperature = weather.maxTemperature,
                            minTemperature = weather.minTemperature,
                            weatherDescription = weather.weatherDescription
                        )
                    }
                    CurrentWeatherAdvanced(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        night = isSystemInDarkTheme(),
                        pressure = weather.pressure,
                        humidity = weather.humidity,
                        visibility = weather.visibility,
                        windDegree = weather.windDegrees,
                        windSpeed = weather.windSpeed,
                        rain = weather.rainPrecipitation
                    )
                }
            }
        }
    }
}
