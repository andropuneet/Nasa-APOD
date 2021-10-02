package org.nasa_apod.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import org.nasa_apod.AppExecutors
import org.nasa_apod.api.ApiInterface
import org.nasa_apod.api.ApiResponse
import org.nasa_apod.db.ImageDao
import org.nasa_apod.db.ImagiaryDb
import org.nasa_apod.utils.AbsentLiveData
import org.nasa_apod.utils.Constants
import org.nasa_apod.vo.Image
import org.nasa_apod.vo.Resource
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val imagiaryDb: ImagiaryDb,
    private val imageDao: ImageDao,
    private val apiInterface: ApiInterface
) {
    //
    fun search(date: String): LiveData<Resource<Image>> {
        return object : NetworkBoundResource<Image>(appExecutors) {
            override fun saveCallResult(item: Image?) {
                if(item!=null) {
                    imagiaryDb.runInTransaction { imageDao.insert(item) }
                }
            }

            override fun shouldFetch(data: Image?): Boolean {
                return data == null
            }

            override fun loadFromDb(): LiveData<Image> {
                return Transformations.switchMap(
                    imageDao.findByDate(date)
                ) { searchData: Image? ->
                    if (searchData == null) {
                        return@switchMap AbsentLiveData.create<Image>()
                    } else {
                        return@switchMap imageDao.findByDate(date)
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<Image>> {
                return apiInterface.getImgResponse(Constants.API_KEY, date)
            }

            override fun processResponse(response: ApiResponse<Image>): Image {
                return response.body!!
            }
        }.asLiveData()
    }
}