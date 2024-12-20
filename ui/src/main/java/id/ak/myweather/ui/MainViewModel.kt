package id.ak.myweather.ui

import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ak.myweather.domain.entity.CurrentWeatherEntity
import id.ak.myweather.domain.use_case.GetCurrentWeather
import id.ak.myweather.ui.state.UiState
import id.ak.myweather.ui.theme.CornflowerBlue
import id.ak.myweather.ui.utils.WeatherUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val getCurrentWeather: GetCurrentWeather) :
    ViewModel() {
    val uiState: StateFlow<UiState>
        field = MutableStateFlow<UiState>(UiState.NotLoading)

    var location by mutableStateOf<Location?>(null)
        private set
    var weather by mutableStateOf<CurrentWeatherEntity?>(null)
        private set
    var backgroundColor by mutableStateOf(CornflowerBlue)
        private set
    var animationRes by mutableStateOf<Int?>(null)
        private set

    fun onErrorShown() {
        uiState.value = UiState.NotLoading
    }

    fun loadOnBackground(action: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            uiState.value = UiState.Loading

            try {
                action()
            } catch (e: Exception) {
                uiState.value = UiState.Error(e.message ?: "Unknown Error")
                e.printStackTrace()
            } finally {
                uiState.value = UiState.NotLoading
            }
        }
    }

    fun setCurrentLocation(location: Location) {
        this.location = location
        fetchCurrentWeather()
    }

    fun fetchCurrentWeather() {
        loadOnBackground {
            location?.let {
                weather = getCurrentWeather(latitude = it.latitude, longitude = it.longitude)
            }
            weather?.let {
                animationRes = WeatherUtils.getAnimationRes(it.weatherName, it.sunrise, it.sunset)
                backgroundColor =
                    WeatherUtils.getBackgroundColor(it.weatherName, it.sunrise, it.sunset)
            }
        }
    }
}