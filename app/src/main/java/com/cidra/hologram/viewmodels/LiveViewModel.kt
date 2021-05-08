package com.cidra.hologram.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cidra.hologram.api.YoutubeService
import com.cidra.hologram.data.LiveItem
import kotlinx.coroutines.launch

enum class YoutubeApiStatus { LOADING, ERROR, DONE }

class LiveViewModel : ViewModel() {


    private val _response = MutableLiveData<List<LiveItem>>()

    val response: LiveData<List<LiveItem>>
        get() = _response

    private val _status = MutableLiveData<YoutubeApiStatus>()

    val status: LiveData<YoutubeApiStatus>
        get() = _status

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    var videoList = mutableListOf<LiveItem>()


    init {
        loadVideo()
    }

    private fun loadVideo() {
        viewModelScope.launch {
            _status.value = YoutubeApiStatus.LOADING
            try {
                videoList = YoutubeService.getLiveItem()
                _response.value = videoList
                _status.value = YoutubeApiStatus.DONE
            } catch (e: Exception) {
                _status.value = YoutubeApiStatus.ERROR
                _response.value = ArrayList()
            }
        }
    }

    /**
     * SwipeRefreshLayout用更新
     */
    fun refresh() {
        viewModelScope.launch {
            videoList = YoutubeService.getLiveItem()
            _response.value = videoList
            _isLoading.value = false
        }
    }

    /**
     * fab並び替え処理
     */
    fun sortByViewer() {
        val sortedVideoList = videoList.sortedByDescending { it.viewers }
        _response.value = sortedVideoList
    }

    fun sortByStartTime() {
        val sortedVideoList = videoList.sortedByDescending { it.startTime }
        _response.value = sortedVideoList
    }


}