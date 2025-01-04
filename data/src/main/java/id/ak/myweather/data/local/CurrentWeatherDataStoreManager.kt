package id.ak.myweather.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import id.ak.myweather.data.model.CurrentWeatherDtoToEntityMapper
import id.ak.myweather.data.model.CurrentWeatherEntityToDtoMapper
import id.ak.myweather.data.model.CurrentWeatherResponse
import id.ak.myweather.domain.entity.GeocodeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("weather")

class CurrentWeatherDataStoreManager(@ApplicationContext private val context: Context) {
    private val weather = stringPreferencesKey("weather")
    private val saveLocation = booleanPreferencesKey("save_location")

    private val dataStore = context.dataStore
    private val gson = Gson()

    val cachedWeather: Flow<CurrentWeatherResponse?> = dataStore.data.map { pref ->
        pref[weather]?.let { data ->
            gson.fromJson(data, CurrentWeatherResponse::class.java)
        }
    }

    val hasSavedLocation: Flow<Boolean> = dataStore.data.map { pref ->
        pref[saveLocation] == true
    }

    suspend fun cacheWeather(
        currentWeatherResponse: CurrentWeatherResponse,
        saveLocation: Boolean
    ) {
        dataStore.edit { pref ->
            pref[weather] = gson.toJson(currentWeatherResponse)
            pref[this.saveLocation] = saveLocation
        }
    }
}