package com.dims.lyrically

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dims.lyrically.database.Favourites
import com.dims.lyrically.databinding.CardItemBinding

class FavouritesRecyclerAdapter: ListAdapter<Favourites, FavouritesRecyclerAdapter.ViewHolder>(FavouritesDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favourite = getItem(position)
        holder.bind(favourite)
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder(private val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(favourite: Favourites) {
            binding.favourite = favourite
            binding.executePendingBindings()
        }
    }
}

class FavouritesDiffCallback: DiffUtil.ItemCallback<Favourites>() {
    override fun areItemsTheSame(oldItem: Favourites, newItem: Favourites): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Favourites, newItem: Favourites): Boolean {
        return oldItem == newItem
    }
}