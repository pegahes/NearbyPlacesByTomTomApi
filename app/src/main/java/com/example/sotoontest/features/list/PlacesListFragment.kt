package com.example.sotoontest.features.list

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sotoontest.R
import com.example.sotoontest.data.list.Places
import com.example.sotoontest.databinding.FragmentPlacesListBinding
import com.example.sotoontest.utils.showIfOrInvisible
import com.example.sotoontest.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

@AndroidEntryPoint
class PlacesListFragment : Fragment(R.layout.fragment_places_list),
    PlacesListPagingAdapter.clickListener {

    private val args by navArgs<PlacesListFragmentArgs>()

    private val viewModel by viewModels<PlacesListViewModel>()

    private var _binding: FragmentPlacesListBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferences: SharedPreferences
    private var sharedPrefName = "latlon"
    private var sharedPrefKey = "location"
    var nullLocationValue = "0"

    private lateinit var placeAdapter: PlacesListPagingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlacesListBinding.bind(view)

        setAdapter()
        getSharedPref()

        binding.apply {
            checkIfNewLocation(args.location)
        }
    }

    private fun setAdapter() {
        placeAdapter = PlacesListPagingAdapter(this)
        placeAdapter.setListener()
    }

    private fun getSharedPref() {
        preferences = this.activity!!.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
    }

    private fun getFromSharedPref(key: String) {

    }

    private fun bindDataToAdapter() {
        binding.apply {
            recyclerPlacesList.apply {
                adapter = placeAdapter.withLoadStateFooter(
                    PlacesListLoadStateAdapter(placeAdapter::retry)
                )
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.searchResults.collectLatest { data ->
                    placeAdapter.submitData(data)
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.hasCurrentLatLonQuery.collect { hasCurrentQuery ->
                    swipeRefreshLayout.isEnabled = hasCurrentQuery

                    if (!hasCurrentQuery) {
                        recyclerPlacesList.isVisible = false
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                placeAdapter.loadStateFlow
                    .distinctUntilChangedBy { it.source.refresh }
                    .filter { it.source.refresh is LoadState.NotLoading }
                    .collect {
                        if (viewModel.pendingScrollToTopAfterNewQuery) {
                            recyclerPlacesList.scrollToPosition(0)
                            viewModel.pendingScrollToTopAfterNewQuery = false
                        }
                        if (viewModel.pendingScrollToTopAfterRefresh && it.mediator?.refresh is LoadState.NotLoading) {
                            recyclerPlacesList.scrollToPosition(0)
                            viewModel.pendingScrollToTopAfterRefresh = false
                        }
                    }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                placeAdapter.loadStateFlow
                    .collect { loadState ->
                        when (val refresh = loadState.mediator?.refresh) {
                            is LoadState.Loading -> {
                                textViewError.isVisible = false
                                buttonRetry.isVisible = false
                                swipeRefreshLayout.isRefreshing = true
                                textViewNoResults.isVisible = false

                                recyclerPlacesList.showIfOrInvisible {
                                    !viewModel.newQueryInProgress && placeAdapter.itemCount > 0
                                }

                                viewModel.refreshInProgress = true
                                viewModel.pendingScrollToTopAfterRefresh = true
                            }
                            is LoadState.NotLoading -> {
                                textViewError.isVisible = false
                                buttonRetry.isVisible = false
                                swipeRefreshLayout.isRefreshing = false
                                recyclerPlacesList.isVisible = placeAdapter.itemCount > 0

                                val noResults =
                                    placeAdapter.itemCount < 1 && loadState.append.endOfPaginationReached
                                            && loadState.source.append.endOfPaginationReached

                                textViewNoResults.isVisible = noResults

                                viewModel.refreshInProgress = false
                                viewModel.newQueryInProgress = false
                            }
                            is LoadState.Error -> {
                                swipeRefreshLayout.isRefreshing = false
                                textViewNoResults.isVisible = false
                                recyclerPlacesList.isVisible = placeAdapter.itemCount > 0

                                val noCachedResults =
                                    placeAdapter.itemCount < 1 && loadState.source.append.endOfPaginationReached

                                textViewError.isVisible = noCachedResults
                                buttonRetry.isVisible = noCachedResults

                                val errorMessage = getString(R.string.could_not_load_search_results)
                                textViewError.text = errorMessage

                                if (viewModel.refreshInProgress) {
                                    showSnackbar(errorMessage)
                                }
                                viewModel.refreshInProgress = false
                                viewModel.newQueryInProgress = false
                                viewModel.pendingScrollToTopAfterRefresh = false
                            }
                            else -> {}
                        }
                    }
            }
            swipeRefreshLayout.setOnRefreshListener {
                placeAdapter.refresh()
            }

            buttonRetry.setOnClickListener {
                placeAdapter.retry()
            }
        }
    }

    private fun checkIfNewLocation(location: String) {
        if (preferences.contains(sharedPrefKey) && location != nullLocationValue) {

            onNewLocation(location)

        } else if (!preferences.contains(sharedPrefKey)) {

            setLatestLatLongToSharedPref(location)

        } else if (preferences.contains(sharedPrefKey) && location == nullLocationValue) {

            viewModel.onNewLatLon(preferences.getString(sharedPrefKey, ""))
            bindDataToAdapter()

        }
    }

    private fun onNewLocation(location: String) {
        val currentLocation = location.split("/")
        val oldLocation = preferences.getString(sharedPrefKey, "")!!.split("/")
        if (distanceBetween(
                currentLocation[0].toDouble(),
                currentLocation[1].toDouble(),
                oldLocation[0].toDouble(),
                oldLocation[1].toDouble(),
            ) > 100F
        ) {
            viewModel.onNewLatLon(location)
            bindDataToAdapter()
        } else {
            viewModel.currentLatLonQuery.value = preferences.getString(sharedPrefKey, "")
            bindDataToAdapter()
        }
    }

    private fun distanceBetween(
        firstLat: Double, firstLon: Double,
        secondLat: Double, secondLon: Double
    ): Float {
        val first = Location("")
        first.latitude = firstLat
        first.longitude = firstLon

        val second = Location("")
        second.latitude = secondLat
        second.longitude = secondLon

        return first.distanceTo(second)

    }


    private fun setLatestLatLongToSharedPref(latLong: String) {
        if (latLong != nullLocationValue) {
            val editor = preferences.edit()
            editor.putString(sharedPrefKey, latLong)
            editor.commit()
            viewModel.onNewLatLon(latLong)
            bindDataToAdapter()
        } else {
            Toast.makeText(context, "You should first turn on your GPS", Toast.LENGTH_LONG).show()
        }
    }


    override fun onClickListener(place: Places) {
        val action =
            PlacesListFragmentDirections.actionPlacesListFragmentToPlacesDetailsFragment(place)
        findNavController().navigate(action)
    }
}
