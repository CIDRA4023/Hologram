package com.cidra.hologram.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cidra.hologram.api.YoutubeService
import com.cidra.hologram.data.NoneItem
import kotlinx.coroutines.launch

class ArchivesViewModel : ViewModel() {

    private val _response = MutableLiveData<List<NoneItem>>()

    val response: LiveData<List<NoneItem>>
        get() = _response

    private val _status = MutableLiveData<YoutubeApiStatus>()

    val status: LiveData<YoutubeApiStatus>
        get() = _status

    private val _isLoading = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    var videoList = mutableListOf<NoneItem>()

    init {
        loadVideo()
    }

    private fun loadVideo() {
        viewModelScope.launch {
            _status.value = YoutubeApiStatus.LOADING
            try {
                videoList = YoutubeService.getArchiveItem()
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
            videoList = YoutubeService.getArchiveItem()
            _response.value = videoList
            _isLoading.value = false
        }
    }


    /**
     * fab並び替え処理
     */
    fun sorByViewer() {
        val sortedVideoList = videoList.sortedByDescending { it.viewers }
        _response.value = sortedVideoList
    }

    fun sorByUpdate() {
        val sortedVideoList = videoList.sortedByDescending { it.publishedAt }
        _response.value = sortedVideoList
    }

    fun sorByGood() {
        val sortedVideoList = videoList.sortedByDescending { it.likeCount }
        _response.value = sortedVideoList
    }

}