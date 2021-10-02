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
package org.nasa_apod.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import org.nasa_apod.AppExecutors
import org.nasa_apod.api.ApiResponse
import org.nasa_apod.utils.Objects
import org.nasa_apod.vo.Resource

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType>
</ResultType> */
abstract class NetworkBoundResource<ResultType> @MainThread internal constructor(private val appExecutors: AppExecutors) {
    private val result = MediatorLiveData<Resource<ResultType>>()
    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (!Objects.equals(result.value, newValue)) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData: ResultType -> setValue(Resource.loading(newData)) }
        result.addSource(apiResponse) { response: ApiResponse<ResultType> ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            if (response.isSuccessful) {
                appExecutors.diskIO().execute {
                    saveCallResult(processResponse(response))
                    appExecutors.mainThread().execute { // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        result.addSource(
                            loadFromDb()
                        ) { newData: ResultType -> setValue(Resource.success(newData)) }
                    }
                }
            } else {
                onFetchFailed()
                result.addSource(
                    dbSource
                ) { newData: ResultType ->
                    setValue(
                        Resource.error(
                            response.errorMessage,
                            newData
                        )
                    )
                }
            }
        }
    }

    protected fun onFetchFailed() {}
    fun asLiveData(): LiveData<Resource<ResultType>> {
        return result
    }

    @WorkerThread
    protected open fun processResponse(response: ApiResponse<ResultType>): ResultType? {
        return response.body
    }

    @WorkerThread
    protected abstract fun saveCallResult(item: ResultType?)
    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean
    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<ResultType>>

    init {
        result.value = Resource.loading(null)
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data: ResultType ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData: ResultType ->
                    setValue(
                        Resource.success(
                            newData
                        )
                    )
                }
            }
        }
    }
}