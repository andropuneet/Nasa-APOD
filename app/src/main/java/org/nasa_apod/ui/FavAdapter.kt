package org.nasa_apod.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.nasa_apod.AppExecutors
import org.nasa_apod.R
import org.nasa_apod.common.DataBoundListAdapter
import org.nasa_apod.databinding.ImageCardItemBinding
import org.nasa_apod.utils.Objects
import org.nasa_apod.vo.Image

class FavAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val appExecutors: AppExecutors,
) : DataBoundListAdapter<Image?, ImageCardItemBinding>(appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Image?>() {

        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return Objects.equals(oldItem.date, newItem.date)
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return (Objects.equals(oldItem.date, oldItem.date)
                    && oldItem.date == newItem.date)
        }
    }) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ImageCardItemBinding {
        return DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                R.layout.image_card_item, parent, false,
                dataBindingComponent
            )
    }
    override fun bind(binding: ImageCardItemBinding, item: Image?) {
        binding.imageInfo= item
    }

}
