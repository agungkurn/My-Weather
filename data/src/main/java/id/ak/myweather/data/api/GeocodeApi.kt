package id.ak.myweather.data.api

import id.ak.myweather.data.model.GeocodeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeApi {
    @GET("direct")
    suspend fun getCoordinatesByName(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5
    ): List<GeocodeResponse>

    @GET("reverse")
    suspend fun getNameByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 1
    ): List<GeocodeResponse>
}