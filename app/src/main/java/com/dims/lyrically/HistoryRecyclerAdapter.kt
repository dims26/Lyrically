package com.dims.lyrically

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dims.lyrically.database.History
import com.dims.lyrically.databinding.ListItemBinding
import com.dims.lyrically.models.Song


class HistoryRecyclerAdapter: ListAdapter<History, HistoryRecyclerAdapter.ViewHolder>(HistoryDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(history: History) {
            binding.song = with(history){
                Song(fullTitle, title, songArtImageThumbnailUrl, url, titleWithFeatured, id, artistName)}
            binding.executePendingBindings()
        }
    }
}

class HistoryDiffCallback: DiffUtil.ItemCallback<History>() {
    override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem == newItem
    }
}