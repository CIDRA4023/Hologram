package com.cidra.hologram.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cidra.hologram.api.FirebaseService
import com.cidra.hologram.data.NoneItem
import com.cidra.hologram.utilities.dateAgo
import com.cidra.hologram.utilities.sdf
import com.cidra.hologram.utilities.truncate
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ArchivesViewModel : ViewModel() {

    private val _response = MutableLiveData<List<NoneItem>>()

    val response: LiveData<List<NoneItem>>
        get() = _response

    private val _status = MutableLiveData<NetworkStatus>()

    val status: LiveData<NetworkStatus>
        get() = _status

    private val _isLoading = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    var videoList = mutableListOf<NoneItem>()

    //
    private var selectedChip: Int = 0


    init {
        loadVideo()
    }

    private fun loadVideo() {
        viewModelScope.launch {
            _status.value = NetworkStatus.LOADING
            try {
                videoList = FirebaseService.getNoneItem()
                _response.value =
                    videoList.filter { truncate(sdf(it.publishedAt)) == truncate(Date()) }
                _status.value = NetworkStatus.DONE
            } catch (e: Exception) {
                Log.e("NoneViewModel", "${e.message}")
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
            videoList = FirebaseService.getNoneItem()
            _response.value =
                videoList.filter { truncate(sdf(it.publishedAt)) == truncate(dateAgo(selectedChip)) }
            _isLoading.value = false
        }
    }

    /**
     * チップで選択した値をクエリ
     */
    fun filterSelectedChip(date: Date, select: Int) {
        val filteredVideoList = videoList.filter { truncate(sdf(it.publishedAt)) == truncate(date) }
        _response.value = filteredVideoList
        selectedChip = select
    }


    /**
     * fab並び替え処理
     */
    fun sorByViewer() {
        val sortedVideoList =
            videoList.filter { truncate(sdf(it.publishedAt)) == truncate(dateAgo(selectedChip)) }
                .sortedByDescending { it.viewers }
        _response.value = sortedVideoList
    }

    fun sorByUpdate() {
        val sortedVideoList =
            videoList.filter { truncate(sdf(it.publishedAt)) == truncate(dateAgo(selectedChip)) }
                .sortedByDescending { it.publishedAt }
        _response.value = sortedVideoList
    }

    fun sorByGood() {
        val sortedVideoList =
            videoList.filter { truncate(sdf(it.publishedAt)) == truncate(dateAgo(selectedChip)) }
                .sortedByDescending { it.likeCount }
        _response.value = sortedVideoList
    }

}