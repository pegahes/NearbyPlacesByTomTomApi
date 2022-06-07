package com.example.sotoontest.data.list

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.sotoontest.api.list.PlacesApi
import retrofit2.HttpException
import java.io.IOException


private const val PLACES_LIST_STARTING_PAGE_INDEX = 1

class PlacesListRemoteMediator (
    private val latLongQuery: String,
    private val placesListApi: PlacesApi,
    private val placesDb: PlacesDatabase,
    private val refreshOnInit: Boolean
) : RemoteMediator<Int, Places>() {

    private val placesDao = placesDb.placesDao()
    private val searchQueryRemoteKeyDao = placesDb.latLongQueryRemoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Places>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> PLACES_LIST_STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> searchQueryRemoteKeyDao.getRemoteKey(latLongQuery).nextPageKey
        }

        try {
            val split = latLongQuery.split("/")

            val response = placesListApi.getNearbyPlaces(split[0].toFloat(), split[1].toFloat(), page, state.config.pageSize)
            val serverSearchResults = response.results

            val lastQueryPosition = placesDao.getLastQueryPosition(latLongQuery) ?: 0
            var queryPosition = lastQueryPosition + 1

            val searchResultArticles = serverSearchResults.map { serverSearchResultArticle ->
                Places(
                    id = serverSearchResultArticle.id,
                    latLonQuery = latLongQuery,
                    dist = serverSearchResultArticle.dist,
                    name = serverSearchResultArticle.name,
                    phone = serverSearchResultArticle.phone,
                    country = serverSearchResultArticle.country,
                    streetName = serverSearchResultArticle.streetName,
                    streetNumber = serverSearchResultArticle.streetNumber,
                    freeformAddress = serverSearchResultArticle.freeformAddress,
                    localName = serverSearchResultArticle.localName,
                    queryPosition = queryPosition
                )
            }

            placesDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    placesDao.deleteSearchResultsForQuery(latLongQuery)
                }

                val nextPageKey = page + 1

                placesDao.insertPlaces(searchResultArticles)
                searchQueryRemoteKeyDao.insertRemoteKey(
                    LatLongQueryRemoteKey(latLongQuery, nextPageKey)
                )
            }
            return MediatorResult.Success(endOfPaginationReached = serverSearchResults.isEmpty())
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return if (refreshOnInit) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

}