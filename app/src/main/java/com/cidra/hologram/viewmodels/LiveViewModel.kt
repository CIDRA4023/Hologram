package com.cidra.hologram.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cidra.hologram.api.FirebaseService
import com.cidra.hologram.data.LiveItem
import kotlinx.coroutines.launch

enum class NetworkStatus { LOADING, ERROR, DONE, NONE}

class LiveViewModel : ViewModel() {


    private val _response = MutableLiveData<List<LiveItem>>()

    val response: LiveData<List<LiveItem>>
        get() = _response

    private val _status = MutableLiveData<NetworkStatus>()

    val status: LiveData<NetworkStatus>
        get() = _status

    // SwipeRefresh用のローディングステート
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    var videoList = mutableListOf<LiveItem>()


    init {
        loadVideo()
    }

    private fun loadVideo() {
        viewModelScope.launch {
            _status.value = NetworkStatus.LOADING
            try {

                videoList = FirebaseService.getLiveItem()
                Log.i("ItemCount", "${videoList.size}")
                _response.value = videoList

                // 配信アイテムがなかった時
                if(videoList.isEmpty()) {
                    _status.value = NetworkStatus.NONE
                    Log.i("NetworkStatus", "None")
                } else {
                    _status.value = NetworkStatus.DONE
                }

            } catch (e: Exception) {
                Log.e("LiveViewModel", "${e.message}")
                _status.value = NetworkStatus.ERROR
                _response.value = ArrayList()
            }
        }
    }

    /**
     * SwipeRefreshLayout用更新
     */
    fun refresh() {
        viewModelScope.launch {
            try {
                FirebaseService.getLiveItem()
                videoList = FirebaseService.getLiveItem()
                _response.value = videoList

                if (videoList.isEmpty()) {
                    _status.value = NetworkStatus.NONE
                } else {
                    _status.value = NetworkStatus.DONE
                }

                // 読み込みが終わったらローディングステートを終了
                _isLoading.value = false
            } catch (e: Exception) {
                _status.value = NetworkStatus.ERROR
                _response.value = ArrayList()
            }
        }
    }

    /**
     * fab並び替え処理
     */
    fun sortByViewer() {
        val sortedVideoList = videoList.sortedByDescending { it.currentViewers.toInt()}
        _response.value = sortedVideoList
    }

    fun sortByStartTime() {
        val sortedVideoList = videoList.sortedByDescending { it.startTime }
        _response.value = sortedVideoList
    }


}