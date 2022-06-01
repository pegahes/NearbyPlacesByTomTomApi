package com.example.sotoontest.data.list

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Places::class], version = 1)
abstract class PlacesDatabase : RoomDatabase() {

    abstract fun placesDao(): PlacesDao
}