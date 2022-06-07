package com.example.sotoontest.data.list

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_query_remote_keys")
data class LatLongQueryRemoteKey  (
    @PrimaryKey val searchQuery: String,
    val nextPageKey: Int
        )