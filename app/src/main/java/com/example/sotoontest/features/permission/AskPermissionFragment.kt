package com.example.sotoontest.features.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sotoontest.R
import com.example.sotoontest.databinding.FragmentAskPermissionBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException


@AndroidEntryPoint
class AskPermissionFragment : Fragment(R.layout.fragment_ask_permission) {

    private var _binding: FragmentAskPermissionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAskPermissionBinding.bind(view)

        context?.let {
            checkIfPermissionGranted(it)
        }

        binding.apply {
            btnShareLocation.setOnClickListener {
                activity?.let {
                    requirePermission()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun FusedLocationProviderClient.awaitCurrentLocation(priority: Int): Location? {
        return suspendCancellableCoroutine {
            val cts = CancellationTokenSource()
            getCurrentLocation(priority, cts.token)
                .addOnSuccessListener {location ->
                    onLocationChanged(location)
                }.addOnFailureListener {e ->
                    it.resumeWithException(e)
                }

            it.invokeOnCancellation {
                cts.cancel()
            }
        }

    }

    private fun onLocationChanged(location: Location) {
        startPlaceListFragment(location)
    }


    private val permissionRequester = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->

        val response = map.entries.first()
        val permission = response.key
        val isGranted = response.value
        when {
            isGranted -> onPermissionGranted()
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission) -> {

                AlertDialog.Builder(requireContext())
                    .setTitle("Warning")
                    .setMessage("You should first let the app to access your location")
                    .setPositiveButton("Access") { _, _ ->
                        requirePermission()
                    }
                    .setNegativeButton("Deny", null)
                    .create()
                    .show()
            }
            else -> {
            }
        }
    }

    private fun onPermissionGranted() {
        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(LocationManagerCompat.isLocationEnabled(lm)) {

            val priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            lifecycleScope.launch {
                val location = LocationServices
                    .getFusedLocationProviderClient(requireContext())
                    .awaitCurrentLocation(priority)
                startPlaceListFragment(location)
            }
        } else {
            startPlaceListFragment(null)
        }
    }

    private fun startPlaceListFragment(location: Location?) {
        location?.let {
            val locationQuery = it.latitude.toString() + "/" + it.longitude
            val action = AskPermissionFragmentDirections.actionPlacesPermissionFragmentToPlacesListFragment(locationQuery)
            findNavController().navigate(action)
        }
        if (location == null){
            val action = AskPermissionFragmentDirections.actionPlacesPermissionFragmentToPlacesListFragment("0")
            findNavController().navigate(action)
        }
    }

    private fun requirePermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        permissionRequester.launch(permissions)
    }

    private fun checkIfPermissionGranted(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            binding.apply {
                btnShareLocation.visibility = View.GONE
            }
            onPermissionGranted()
        }
    }

}

