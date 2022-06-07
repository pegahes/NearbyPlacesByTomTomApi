package com.example.sotoontest.api.list

import com.example.sotoontest.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface PlacesApi {

    companion object {
        const val BASE_URL = "https://api.tomtom.com/"
        const val API_KEY = BuildConfig.TOMTOM_API_ACCESS_KEY
    }

    @GET("search/2/nearbySearch/.json")
    suspend fun getNearbyPlaces(
        @Query("lat") lat: Float = 37.337F,
        @Query("lon") lon: Float = -121.89F,
        @Query("limit") limit: Int = 10,
        @Query("ofs") ofs: Int = 0,
        @Query ("key") api_key: String = API_KEY
        ): PlacesResponse

}