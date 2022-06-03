package com.example.sotoontest.data.list

import com.example.sotoontest.api.list.PlacesApi
import javax.inject.Inject

class PlacesRepository @Inject constructor(
    private val placesApi: PlacesApi,
    private val placesDb: PlacesDatabase
) {
    private val placesDao = placesDb.placesDao()
}