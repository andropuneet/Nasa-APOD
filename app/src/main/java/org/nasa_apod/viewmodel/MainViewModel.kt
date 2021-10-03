package org.nasa_apod.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nasa_apod.repository.ImageRepository
import org.nasa_apod.utils.AbsentLiveData
import org.nasa_apod.vo.Image
import org.nasa_apod.vo.Resource
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(var repository: ImageRepository) : ViewModel() {
    val date = MutableLiveData<String>()
    val data: LiveData<Resource<Image>> = Transformations.switchMap(date) { input: String ->
        if (input.isEmpty()) {
            return@switchMap AbsentLiveData.create<Resource<Image>>()
        }
        Timber.d("Date"+input);
        repository.search(input)
    }



    fun retry() {
        val current = date.value
        if (current != null && !current.isEmpty()) {
            date.value = current
        }
    }

    fun saveToFav(imageInfo: Image):MutableLiveData<Resource<Image>>{
       return repository.markAsFav(imageInfo)
    }

    fun getFavList() : LiveData<List<Image>>{
        return  repository.getFavList()
    }

}