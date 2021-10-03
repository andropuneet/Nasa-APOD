package org.nasa_apod.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import org.json.JSONException
import org.json.JSONObject
import org.nasa_apod.AppExecutors
import org.nasa_apod.R
import org.nasa_apod.api.ApiResponse
import org.nasa_apod.utils.NetworkMonitor
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
abstract class NetworkBoundResource<ResultType> @MainThread internal constructor(private val appExecutors: AppExecutors, private val networkMonitor: NetworkMonitor) {
    private val result = MediatorLiveData<Resource<ResultType>>()
    var errorMessage = "You don\'t have any proper internet connection.\nPlease Retry!!"

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
                    run {
                        var msg = ""
                        if(response.errorMessage != null) {
                            try {
                                msg = JSONObject(response.errorMessage!!)["msg"] as String
                            }catch (e :JSONException){
                                msg = response.errorMessage!!
                            }
                        }
                        if (!networkMonitor.isConnected) {
                            msg = errorMessage
                        }
                        setValue(
                                Resource.error(
                                        msg,
                                        newData
                                )
                        )
                    }
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