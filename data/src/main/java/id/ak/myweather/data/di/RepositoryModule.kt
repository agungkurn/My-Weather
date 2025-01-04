package id.ak.myweather.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.ak.myweather.data.repository.DefaultGeocodingRepository
import id.ak.myweather.data.repository.DefaultWeatherRepository
import id.ak.myweather.domain.repository.GeocodingRepository
import id.ak.myweather.domain.repository.WeatherRepository

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindWeatherRepository(repository: DefaultWeatherRepository): WeatherRepository

    @Binds
    fun bindGeocodingRepository(repository: DefaultGeocodingRepository): GeocodingRepository
}