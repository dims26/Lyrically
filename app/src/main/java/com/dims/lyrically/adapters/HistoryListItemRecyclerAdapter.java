package com.dims.lyrically.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dims.lyrically.R;
import com.dims.lyrically.database.History;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HistoryListItemRecyclerAdapter extends RecyclerView.Adapter<HistoryListItemRecyclerAdapter.ListItemHolder> {

    public ArrayList<History> mHistory = new ArrayList<>();

    @NonNull
    @Override
    public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListItemHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {
        History history = mHistory.get(position);

        holder.titleFeaturedTextView.setText(history.getTitleWithFeatured());
        holder.artistTextView.setText(history.getArtistName());
        if (history.getSongArtImageThumbnailUrl() != null && !history.getSongArtImageThumbnailUrl().equals(""))
            Picasso.get().load(history.getSongArtImageThumbnailUrl()).into(holder.songThumbnailImageView);


    }

    @Override
    public int getItemCount() {
        return mHistory.size();
    }

    class ListItemHolder extends RecyclerView.ViewHolder{


        ImageView songThumbnailImageView;
        TextView titleFeaturedTextView, artistTextView;

        ListItemHolder(@NonNull final View itemView) {
            super(itemView);

            songThumbnailImageView = itemView.findViewById(R.id.listItem_song_thumbnail_imageView);
            titleFeaturedTextView = itemView.findViewById(R.id.listItem_titleFeatured_textView);
            artistTextView = itemView.findViewById(R.id.listItem_artist_textView);
        }
    }
}

