/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nasa_apod.binding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import org.nasa_apod.R
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {
    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun bindImage(imageView: ImageView, url: String?) {
        Timber.d(url)
        if(url!=null) {
            if (url.contains("youtube")) {
                Glide.with(imageView.context).load(url).placeholder(R.drawable.video_image)
                    .into(imageView)
            } else {
                Glide.with(imageView.context).load(url).placeholder(R.drawable.image_placeholder)
                    .into(imageView)
            }
        }else{
            Glide.with(imageView.context).load("").placeholder(R.drawable.image_placeholder)
                .into(imageView)
        }
    }
    @JvmStatic
    @BindingAdapter("dateFormat")
    fun bindDateFormat(textView: TextView, date: String?) {
        var list : List<String>? = date?.split("-")
        var dateFormat = SimpleDateFormat("dd MMM, yyyy",Locale.US)
        var calendar = Calendar.getInstance()
        if(list!=null) {
            calendar.set(list[0].toInt(), list[1].toInt()-1, list[2].toInt())
            var date: Date = calendar.time
            textView.text = dateFormat.format(date).toString()
        }
    }
    @JvmStatic
    @BindingAdapter("markFav")
    fun bindMarkFav(imageView: ImageView, isFav: Boolean) {
        if(isFav){
            Glide.with(imageView.context).load(R.drawable.fav_selected).into(imageView)
        }else{
            Glide.with(imageView.context).load(R.drawable.fav_border).into(imageView)
        }
    }
}