package com.example.sotoontest.features.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sotoontest.R
import com.example.sotoontest.databinding.PlacesListLoadStateFooterBinding

class PlacesListLoadStateAdapter (private val retry: ()->Unit) :
    LoadStateAdapter<PlacesListLoadStateAdapter.LoadStateViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding =
            PlacesListLoadStateFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return LoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {

        holder.bind(loadState)
    }

    inner class LoadStateViewHolder(private val binding: PlacesListLoadStateFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.fragmentPlaceListRetryBtn.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {

            binding.apply {
                fragmentPlaceListProgressbar.isVisible = loadState is LoadState.Loading
                fragmentPlaceListRetryBtn.isVisible = loadState is LoadState.Error
                fragmentPlaceListRetryText.isVisible = loadState is LoadState.Error

                if (loadState is LoadState.Error) {
                    fragmentPlaceListRetryText.text = loadState.error.localizedMessage
                        ?: binding.root.context.getString(R.string.unknown_error_occurred)
                }
            }
        }
    }
}