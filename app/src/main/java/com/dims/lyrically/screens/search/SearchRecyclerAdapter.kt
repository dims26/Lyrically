package com.dims.lyrically.screens.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dims.lyrically.databinding.ListItemBinding
import com.dims.lyrically.models.Song
import com.dims.lyrically.utils.picasso
import com.squareup.picasso.Picasso

class SearchRecyclerAdapter(private val picasso: Picasso): ListAdapter<Song, SearchRecyclerAdapter.ViewHolder>(SearchDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), picasso)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder(private val binding: ListItemBinding, private val picasso: Picasso) : RecyclerView.ViewHolder(binding.root){
        fun bind(song: Song) {
            binding.song = song
            binding.picasso = picasso
            binding.executePendingBindings()
        }
    }
}

class SearchDiffCallback: DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }
}