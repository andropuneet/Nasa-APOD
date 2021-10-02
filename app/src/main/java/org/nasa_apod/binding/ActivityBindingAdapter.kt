package org.nasa_apod.binding

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import javax.inject.Inject

class ActivityBindingAdapter @Inject constructor(val context: Context) {
    @BindingAdapter("imassgeUrl")
    fun bindImage(imageView: ImageView, url: String?) {
        Glide.with(context).load(url).into(imageView)
    }
}