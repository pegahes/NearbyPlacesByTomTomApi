package com.example.sotoontest.features.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sotoontest.data.list.Places
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PlacesListViewModel @Inject constructor (
        ) : ViewModel() {

    private val placesFlow = MutableStateFlow<List<Places>>(emptyList())
    val places: Flow<List<Places>> = placesFlow

    init {
        viewModelScope.launch {

        }
    }
}