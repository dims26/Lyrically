package com.dims.lyrically.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dims.lyrically.R;
import com.dims.lyrically.database.Favourites;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class FavouritesCardItemRecyclerAdapter extends
        RecyclerView.Adapter<FavouritesCardItemRecyclerAdapter.ViewHolder> {

    public List<Favourites> mFavourites = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(cardItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Favourites favourite = mFavourites.get(position);


        holder.titleFeaturedTextView.setText(favourite.getTitleWithFeatured());
        holder.artistTextView.setText(favourite.getArtistName());
        if (favourite.getSongArtImageThumbnailUrl() != null && !favourite.getSongArtImageThumbnailUrl().equals(""))
            Picasso.get().load(favourite.getSongArtImageThumbnailUrl()).into(holder.songThumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return mFavourites.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        ImageView songThumbnailImageView;
        TextView titleFeaturedTextView, artistTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            songThumbnailImageView = itemView.findViewById(R.id.cardItem_song_thumbnail_imageView);
            titleFeaturedTextView = itemView.findViewById(R.id.cardItem_titleFeatured_textView);
            artistTextView = itemView.findViewById(R.id.cardItem_artist_textView);
        }
    }
}