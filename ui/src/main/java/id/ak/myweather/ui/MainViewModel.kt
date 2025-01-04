package id.ak.myweather.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ak.myweather.domain.entity.CurrentWeatherEntity
import id.ak.myweather.domain.entity.GeocodeEntity
import id.ak.myweather.domain.use_case.GetCoordinatesByName
import id.ak.myweather.domain.use_case.GetCurrentWeather
import id.ak.myweather.domain.use_case.HasSavedLocation
import id.ak.myweather.ui.state.UiState
import id.ak.myweather.ui.theme.CornflowerBlue
import id.ak.myweather.ui.utils.WeatherUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeather,
    private val getCoordinatesByNameUseCase: GetCoordinatesByName,
    private val hasSavedLocationUseCase: HasSavedLocation
) : ViewModel() {
    val uiState: StateFlow<UiState>
        field = MutableStateFlow<UiState>(UiState.NotLoading)

    val weather = getCurrentWeatherUseCase.cachedWeather
    val hasSavedLocation = hasSavedLocationUseCase.value

    var isLoadingSearch by mutableStateOf(false)
        private set
    var locations by mutableStateOf(listOf<GeocodeEntity>())
        private set


    fun onErrorShown() {
        uiState.value = UiState.NotLoading
    }

    private fun loadOnBackground(
        onLoading: (Boolean) -> Unit = {
            uiState.value = if (it) UiState.Loading else UiState.NotLoading
        },
        onError: (Exception) -> Unit = {
            uiState.value = UiState.Error(it.message ?: "Unknown Error")
            it.printStackTrace()
        },
        action: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch {
            onLoading(true)
            try {
                action()
            } catch (e: Exception) {
                onError(e)
            } finally {
                onLoading(false)
            }
        }
    }

    fun fetchCurrentWeather(latitude: Double, longitude: Double, isUserSelected: Boolean) {
        loadOnBackground {
            getCurrentWeatherUseCase(
                latitude = latitude,
                longitude = longitude,
                saveLocation = isUserSelected
            )
        }
    }

    fun searchLocation(query: String) {
        loadOnBackground(onLoading = { isLoadingSearch = it }) {
            locations = getCoordinatesByNameUseCase(query)
        }
    }
}