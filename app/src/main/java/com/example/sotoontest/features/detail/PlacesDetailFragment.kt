package com.example.sotoontest.features.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.sotoontest.R
import com.example.sotoontest.databinding.FragmentPlacesDetailBinding

class PlacesDetailFragment : Fragment(R.layout.fragment_places_detail){
    private val args by navArgs<PlacesDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPlacesDetailBinding.bind(view)

        binding.apply {
            args.place.name?.let {
                textViewTitle.text = it
                textViewTitle.visibility = View.VISIBLE
            }
            args.place.phone?.let {
                textViewPhone.text = it
                textViewPhone.visibility = View.VISIBLE
            }
            args.place.dist?.let {
                textViewDist.text = it.toString() + " (meter)"
                textViewDist.visibility = View.VISIBLE
            }
            args.place.freeformAddress?.let {
                textViewAddress.text = it
                textViewAddress.visibility = View.VISIBLE
            }
        }
    }
}