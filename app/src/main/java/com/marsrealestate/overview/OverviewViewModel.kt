package com.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marsrealestate.network.MarsApi
import com.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

enum class MarsApiStatus{ LOADING, ERROR, DONE }

class OverviewViewModel: ViewModel() {
    // The internal MutableLiveData String that stores the most recent response
    private val _status = MutableLiveData<MarsApiStatus>()

    // The external immutable LiveData for the response String
    val status: MutableLiveData<MarsApiStatus>
        get() = _status

    private val _properties = MutableLiveData<List<MarsProperty>>()
    val property: LiveData<List<MarsProperty>>
        get() = _properties

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope ( viewModelJob + Dispatchers.Main )

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties()
    }


    /**
     * Sets the value of the response LiveData to the Mars API status or the successful number of
     * Mars properties retrieved.
     */
    private fun getMarsRealEstateProperties() {
        coroutineScope.launch {
            // Get the Deferred object for our retrofit request
            var getPropertiesDefered = MarsApi.retrofitService.getProperties()
            try {
                _status.value = MarsApiStatus.LOADING

                // Await the completion of our retrofit request
                var listResult = getPropertiesDefered.await()
                _status.value = MarsApiStatus.DONE
                _properties.value = listResult

            }
            catch (e: Exception){
                _status.value = MarsApiStatus.ERROR
                _properties.value = ArrayList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}






