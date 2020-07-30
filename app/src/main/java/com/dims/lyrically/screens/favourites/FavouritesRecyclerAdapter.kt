package com.dims.lyrically.screens.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dims.lyrically.models.Favourites
import com.dims.lyrically.databinding.CardItemBinding
import com.squareup.picasso.Picasso

class FavouritesRecyclerAdapter(private val picasso: Picasso): ListAdapter<Favourites, FavouritesRecyclerAdapter.ViewHolder>(FavouritesDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), picasso)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favourite = getItem(position)
        holder.bind(favourite)
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder(private val binding: CardItemBinding, private val picasso: Picasso) : RecyclerView.ViewHolder(binding.root){
        fun bind(favourite: Favourites) {
            binding.favourite = favourite
            binding.picasso = picasso
            binding.executePendingBindings()
        }
    }
}

class FavouritesDiffCallback: DiffUtil.ItemCallback<Favourites>() {
    override fun areItemsTheSame(oldItem: Favourites, newItem: Favourites): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Favourites, newItem: Favourites): Boolean {
        return oldItem == newItem
    }
}