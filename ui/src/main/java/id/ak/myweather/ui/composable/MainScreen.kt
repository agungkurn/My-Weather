package id.ak.myweather.ui.composable

import android.annotation.SuppressLint
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.rememberLottieRetrySignal
import id.ak.myweather.domain.entity.GeocodeEntity
import id.ak.myweather.ui.MainViewModel
import id.ak.myweather.ui.R
import id.ak.myweather.ui.state.UiState
import id.ak.myweather.ui.theme.CornflowerBlue
import id.ak.myweather.ui.utils.WeatherUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    showLocationSheet: Boolean,
    setShowLocationSheet: (Boolean) -> Unit,
    requestLocationPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<MainViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val lottieRetrySignal = rememberLottieRetrySignal()
    val snackbarHostState = remember { SnackbarHostState() }
    val locationSheetState = rememberModalBottomSheetState()

    val currentWeather by viewModel.weather.collectAsStateWithLifecycle(null)
    val hasSavedLocation by viewModel.hasSavedLocation.collectAsStateWithLifecycle(null)
    var backgroundColor by remember { mutableStateOf(CornflowerBlue) }
    var animationRes by remember { mutableStateOf<Int?>(null) }

    val contentColor by remember(backgroundColor) {
        derivedStateOf {
            if (backgroundColor.luminance() > .5f) Color.Black else Color.White
        }
    }

    LaunchedEffect(currentWeather) {
        currentWeather?.let {
            animationRes = WeatherUtils.getAnimationRes(it.weatherName, it.sunrise, it.sunset)
            backgroundColor =
                WeatherUtils.getBackgroundColor(it.weatherName, it.sunrise, it.sunset)
            viewModel.fetchCurrentWeather(
                latitude = it.latitude,
                longitude = it.longitude,
                isUserSelected = hasSavedLocation == true
            )
        } ?: if (hasSavedLocation != true) {
            requestLocationPermission()
        } else {
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = backgroundColor,
        contentColor = contentColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        if (showLocationSheet) {
            LocationBottomSheet(
                sheetState = locationSheetState,
                locations = viewModel.locations,
                onSearch = viewModel::searchLocation,
                onUseGnss = requestLocationPermission,
                onSelect = {
                    viewModel.fetchCurrentWeather(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        isUserSelected = true
                    )
                    coroutineScope.launch {
                        locationSheetState.hide()
                    }
                },
                onDismissRequest = { setShowLocationSheet(false) }
            )
        }

        PullToRefreshBox(
            modifier = Modifier
                .padding(it)
                .consumeWindowInsets(it),
            isRefreshing = uiState == UiState.Loading,
            onRefresh = {
                hasSavedLocation?.let { locationSaved ->
                    if (locationSaved && currentWeather != null) {
                        viewModel.fetchCurrentWeather(
                            latitude = currentWeather!!.latitude,
                            longitude = currentWeather!!.longitude,
                            isUserSelected = true
                        )
                    } else {
                        requestLocationPermission()
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                TextButton(onClick = { setShowLocationSheet(true) }) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = currentWeather?.locationName
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentWeather?.locationName ?: "-",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                currentWeather?.let { weather ->
                    animationRes?.let {
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
                        modifier = Modifier.fillMaxWidth(),
                        night = isSystemInDarkTheme(),
                        pressure = weather.pressure,
                        humidity = weather.humidity,
                        visibility = weather.visibility,
                        windDegree = weather.windDegrees,
                        windSpeed = weather.windSpeed,
                        rain = weather.rainPrecipitation
                    )
                    SunInfographic(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        dark = isSystemInDarkTheme(),
                        sunrise = weather.sunrise,
                        sunset = weather.sunset
                    )
                    WeatherForecast(
                        modifier = Modifier.padding(vertical = 16.dp),
                        items = viewModel.weatherForecast
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationBottomSheet(
    sheetState: SheetState,
    locations: List<GeocodeEntity>,
    onUseGnss: () -> Unit,
    onSearch: (String) -> Unit,
    onSelect: (GeocodeEntity) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val ime = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = LocalView.current
    val focusRequester = remember { FocusRequester() }

    var query by remember { mutableStateOf("") }

    LaunchedEffect(sheetState.isVisible) {
        if (sheetState.isVisible) {
            query = ""
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(query) {
        if (query.trim().isNotEmpty()) {
            delay(300)
            onSearch(query)
        }
    }

    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        AnimatedVisibility(query.isEmpty()) {
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onUseGnss)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Text(text = stringResource(R.string.use_current_location))
                }
                Box(contentAlignment = Alignment.Center) {
                    HorizontalDivider()
                    Text(
                        modifier = Modifier
                            .background(BottomSheetDefaults.ContainerColor)
                            .padding(horizontal = 8.dp),
                        text = "or",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        TextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = query,
            onValueChange = { query = it },
            placeholder = { Text(text = stringResource(R.string.search_placeholder)) }
        )
        LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(16.dp)) {
            if (locations.isNotEmpty()) {
                items(locations) {
                    val name = remember(it) {
                        listOf(it.name, it.state, it.country).filterNotNull().joinToString()
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                ime.hideSoftInputFromWindow(view.windowToken, 0)
                                onSelect(it)
                            }
                            .padding(16.dp),
                        text = name,
                        maxLines = 1
                    )
                }
            }
        }
    }
}