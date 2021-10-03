package org.nasa_apod.binding

import android.content.Context
import androidx.databinding.DataBindingComponent

class ActivityDataBindingComponent(context: Context) : DataBindingComponent {
    private var adapter: ActivityBindingAdapter = ActivityBindingAdapter(context)
    override fun getActivityBindingAdapter(): ActivityBindingAdapter {
        return adapter
    }
}