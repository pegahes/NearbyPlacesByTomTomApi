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
        @Query("lat") lat: Float = 40.756619F,
        @Query("lon") lon: Float = -73.978430F,
        @Query("limit") limit: Int,
        @Query("ofs") ofs: Int,
        @Query("radius") radius: Int? = 2000,
        @Query ("key") api_key: String = API_KEY
        ): PlacesResponse

}