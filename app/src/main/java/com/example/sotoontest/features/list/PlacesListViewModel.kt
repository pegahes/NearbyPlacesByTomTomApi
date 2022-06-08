package com.example.sotoontest.features.list

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.sotoontest.data.list.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class PlacesListViewModel @Inject constructor (
    private val repository: PlacesRepository,
    state: SavedStateHandle
        ) : ViewModel() {

    val currentLatLonQuery = state.getLiveData<String?>("currentLatLonQuery", null)
    val hasCurrentLatLonQuery = currentLatLonQuery.asFlow().map { it != null }

    var refreshInProgress = false
    var pendingScrollToTopAfterRefresh = false

    var newQueryInProgress = false
    var pendingScrollToTopAfterNewQuery = false

    private var refreshOnInit = false

    val searchResults = currentLatLonQuery.asFlow().flatMapLatest { query ->
        query?.let {
            repository.getServerResultsPaged(query, refreshOnInit)
        } ?: emptyFlow()
    }.cachedIn(viewModelScope)

    fun onNewLatLon(query: String?) {
        refreshOnInit = true
        currentLatLonQuery.value = query
        newQueryInProgress = true
        pendingScrollToTopAfterNewQuery = true
    }
}