package com.example.sotoontest.features.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sotoontest.data.list.Places
import com.example.sotoontest.databinding.ItemPlaceListBinding

class PlacesListPagingAdapter( val listener: clickListener)
    :PagingDataAdapter<Places, PlacesListPagingAdapter.PlacesListAdapterViewHolder>(PlacesComparator()) {

    lateinit var _listener: clickListener

    fun setListener(){
        this._listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesListAdapterViewHolder {
        val binding =
            ItemPlaceListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacesListAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlacesListAdapterViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem, _listener)

        }
    }

    class PlacesListAdapterViewHolder(private val binding: ItemPlaceListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(place: Places, _listener: clickListener) {
            binding.apply {
                textViewTitle.text = place.name
                textViewDistance.text = (place.dist?.toInt()).toString()+ " meter"
                binding.root.setOnClickListener {
                    _listener.onClickListener(place)
                }
            }
        }
    }

    class PlacesComparator : DiffUtil.ItemCallback<Places>() {
        override fun areItemsTheSame(oldItem: Places, newItem: Places) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Places, newItem: Places) =
            oldItem == newItem
    }
    interface clickListener {
        fun onClickListener(place: Places)
    }

}