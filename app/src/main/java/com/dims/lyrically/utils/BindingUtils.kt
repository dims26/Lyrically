package com.dims.lyrically.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.dims.lyrically.R
import com.squareup.picasso.Picasso

@BindingAdapter("titleWithFeatured") fun titleWithFeatured(textView: TextView, title: String){ textView.text = title }
@BindingAdapter("artistName") fun artistName(textView: TextView, artistName: String){ textView.text = artistName }
@BindingAdapter("thumbnail") fun thumbnail(imageView: ImageView, url: String){
    if (url.isNotEmpty() and !url.contains("default_cover_image.png")){
        Picasso.get().load(url)
                .fit().centerCrop().noFade()
                .placeholder(R.drawable.loader)
                .error(R.drawable.no_image_bubbles)
                .into(imageView) }
    else{
        Picasso.get().load(R.drawable.no_image_bubbles)
                .fit().centerCrop().noFade()
                .placeholder(R.drawable.loader)
                .into(imageView) }
}