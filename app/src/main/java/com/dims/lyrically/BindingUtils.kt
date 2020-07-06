package com.dims.lyrically

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("titleWithFeatured") fun titleWithFeatured(textView: TextView, title: String){ textView.text = title }
@BindingAdapter("artistName") fun artistName(textView: TextView, artistName: String){ textView.text = artistName }
@BindingAdapter("thumbnail") fun thumbnail(imageView: ImageView, url: String){
    if (url.isNotEmpty())
        Picasso.get().load(url).into(imageView) }