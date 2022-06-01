package com.example.sotoontest.data.list

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacesDao {

    @Query("SELECT * FROM places")
    fun getAllPlaces(): Flow<List<Places>>

    @Query("SELECT * FROM places WHERE id=:id")
    fun getOnePlace(id: String): Flow<Places>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(places: List<Places>)

    @Query("DELETE FROM places")
    suspend fun deleteAllPlaces()
}