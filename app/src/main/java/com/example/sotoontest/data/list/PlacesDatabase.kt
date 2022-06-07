package com.example.sotoontest.data.list

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Places::class, LatLongQueryRemoteKey::class], version = 3)
abstract class PlacesDatabase : RoomDatabase() {

    abstract fun placesDao(): PlacesDao

    abstract fun latLongQueryRemoteKeyDao(): LatLongQueryRemoteKeyDao
}
