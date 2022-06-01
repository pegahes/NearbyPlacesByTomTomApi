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

    @Headers("key: $API_KEY")
    @GET("search/{versionNumber}/nearbySearch/.{ext}")
    suspend fun getNearbyPlaces(
        @Path("versionNumber") versionNumber: String,
        @Path("ext") ext: String,
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("limit") limit: Int,
        @Query("ofs") ofs: Int
    ): PlacesResponse

}