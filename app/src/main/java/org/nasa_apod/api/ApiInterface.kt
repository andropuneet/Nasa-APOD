package org.nasa_apod.api

import androidx.lifecycle.LiveData
import org.nasa_apod.vo.Image
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("/planetary/apod")
    fun getImgResponse(
        @Query("key") key: String,
        @Query("date") date: String
    ): LiveData<ApiResponse<Image>>
}