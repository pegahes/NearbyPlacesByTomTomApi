package com.example.sotoontest.data.list

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacesDao {

    @Query("SELECT * FROM places_table")
    fun getAllPlaces(): Flow<List<Places>>

    @Query("SELECT * FROM places_table WHERE id=:id")
    fun getOnePlace(id: String): Flow<Places>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(places: List<Places>)

    @Query("DELETE FROM places_table")
    suspend fun deleteAllPlaces()

    @Query("SELECT * FROM places_table WHERE latLonQuery = :query")
    fun getSearchNearbyResultPaged(query: String): PagingSource<Int, Places>

    @Query("DELETE FROM places_table WHERE latLonQuery = :query")
    suspend fun deleteSearchResultsForQuery(query: String)

    @Query("SELECT MAX(queryPosition) FROM places_table WHERE latLonQuery = :searchQuery")
    suspend fun getLastQueryPosition(searchQuery: String): Int?
}