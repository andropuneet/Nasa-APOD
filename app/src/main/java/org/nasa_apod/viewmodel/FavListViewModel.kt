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

class FavListViewModel @Inject constructor(var repository: ImageRepository) : ViewModel() {

    fun getFavList() : LiveData<List<Image>>{
        return  repository.getFavList()
    }

}