package com.example.sotoontest.features.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sotoontest.data.list.Places
import com.example.sotoontest.databinding.ItemPlaceListBinding

class PlacesListAdapter : ListAdapter<Places, PlacesListAdapter.PlacesListAdapterViewHolder>(PlacesComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesListAdapterViewHolder {
        val binding =
            ItemPlaceListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacesListAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlacesListAdapterViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    class PlacesListAdapterViewHolder(private val binding: ItemPlaceListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(place: Places) {
            binding.apply {
                textViewTitle.text = place.name
                textViewDistance.text = place.dist.toString()
            }
        }
    }

    class PlacesComparator : DiffUtil.ItemCallback<Places>() {
        override fun areItemsTheSame(oldItem: Places, newItem: Places) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Places, newItem: Places) =
            oldItem == newItem
    }
}