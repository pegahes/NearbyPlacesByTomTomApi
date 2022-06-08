package com.example.sotoontest.data.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.sotoontest.api.list.PlacesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlacesRepository @Inject constructor(
    private val placesApi: PlacesApi,
    private val placesDb: PlacesDatabase
) {
    private val placesDao = placesDb.placesDao()

    fun getServerResultsPaged(
        latLongQuery: String,
        refreshOnInit: Boolean
    ): Flow<PagingData<Places>> =
        Pager(
            config = PagingConfig(pageSize = 20, maxSize = 200),
            remoteMediator = PlacesListRemoteMediator(latLongQuery, placesApi, placesDb, refreshOnInit),
            pagingSourceFactory = { placesDao.getSearchNearbyResultPaged(latLongQuery) }
        ).flow

}
