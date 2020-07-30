package com.dims.lyrically.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.dims.lyrically.R
import com.squareup.picasso.Picasso

val picasso: Picasso = Picasso.get()

@BindingAdapter("titleWithFeatured") fun titleWithFeatured(textView: TextView, title: String){ textView.text = title }
@BindingAdapter("artistName") fun artistName(textView: TextView, artistName: String){ textView.text = artistName }
@BindingAdapter("thumbnail", "picasso") fun thumbnail(imageView: ImageView, url: String, picasso: Picasso){
    //load cover image from server
    if (url.isNotEmpty() and !url.contains("default_cover_image.png")){
        picasso.load(url)
                .fit().centerCrop().noFade()
                .placeholder(R.drawable.loader)
                .error(R.drawable.no_image_bubbles)
                .into(imageView)}
    else{
        //load local cover image
        picasso.load(R.drawable.no_image_bubbles)
                .fit().centerCrop().noFade()
                .placeholder(R.drawable.loader)
                .into(imageView) }
}