package org.nasa_apod.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nasa_apod.repository.ImageRepository
import org.nasa_apod.utils.AbsentLiveData
import org.nasa_apod.vo.Image
import org.nasa_apod.vo.Resource
import javax.inject.Inject

class MainViewModel @Inject constructor(repository: ImageRepository) : ViewModel() {
    private val date = MutableLiveData<String>()
    val data: LiveData<Resource<Image>> = Transformations.switchMap(date) { input: String? ->
        if (input!!.isEmpty()) {
            return@switchMap AbsentLiveData.create<Resource<Image>>()
        }
        repository.search(input)
    }

    fun retry() {
        val current = date.value
        if (current != null && !current.isEmpty()) {
            date.value = current
        }
    }

    fun refresh() {
        if (date.value != null) {
            date.value = date.value
        }
    }

}