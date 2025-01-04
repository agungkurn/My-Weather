package id.ak.myweather.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.ak.myweather.data.local.CurrentWeatherDataStoreManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {
    @Provides
    @Singleton
    fun provideWeatherDataStore(@ApplicationContext context: Context): CurrentWeatherDataStoreManager {
        return CurrentWeatherDataStoreManager(context)
    }
}