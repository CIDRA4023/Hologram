package com.cidra.hologram.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cidra.hologram.api.FirebaseService
import com.cidra.hologram.data.UpcomingItem
import com.cidra.hologram.utilities.sdf
import com.cidra.hologram.utilities.tomorrow
import com.cidra.hologram.utilities.truncate
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ScheduleViewModel : ViewModel() {

    private val _response = MutableLiveData<List<UpcomingItem>>()
    private val _todayItem = MutableLiveData<List<UpcomingItem>>()
    val todayItem: LiveData<List<UpcomingItem>>
        get() = _todayItem

    private val _tomorrowItem = MutableLiveData<List<UpcomingItem>>()
    val tomorrowItem: LiveData<List<UpcomingItem>>
        get() = _tomorrowItem


    private val _status = MutableLiveData<NetworkStatus>()

    val status: LiveData<NetworkStatus>
        get() = _status

    private val _isLoading = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        loadVideo()
    }


    private fun loadVideo() {
        viewModelScope.launch {
            _status.value = NetworkStatus.LOADING
            try {
                val getVideoList = FirebaseService.getUpcomingItem()
//                _response.value = getVideoList
                _status.value = NetworkStatus.DONE

                // 今日と明日のアイテムに分離
                _todayItem.value =
                    getVideoList.filter { truncate(sdf(it.scheduledStartTime)) == truncate(Date()) }
                _tomorrowItem.value =
                    getVideoList.filter { truncate(sdf(it.scheduledStartTime)) == truncate(tomorrow()) }
                Log.i("filterTodayList", "${_todayItem.value}")
                Log.i("filterTomorrowList", "${_tomorrowItem.value}")

            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "${e.message}")
                _status.value = NetworkStatus.ERROR
                _todayItem.value = ArrayList()
                _tomorrowItem.value = ArrayList()
            }
        }
    }

    /**
     * Adapterでアイテム数を取得
     */
    fun getResponseItem(position: Int): UpcomingItem = _response.value?.get(position)
        ?: throw IllegalStateException("ContentList is not Initialized")


    fun getTodayItemCount(): Int = _todayItem.value?.size ?: 0

    fun getTomorrowItemCount(): Int = _tomorrowItem.value?.size ?: 0


    /**
     * SwipeRefreshLayout用更新
     */
    fun refresh() {
        viewModelScope.launch {
            val getVideoList = FirebaseService.getUpcomingItem()
            _todayItem.value =
                getVideoList.filter { truncate(sdf(it.scheduledStartTime)) == truncate(Date()) }
            _tomorrowItem.value =
                getVideoList.filter { truncate(sdf(it.scheduledStartTime)) == truncate(tomorrow()) }
            //_response.value = FirebaseService.getUpcomingItem()
//            Log.i("response", "${_response.value}")
            _isLoading.value = false
        }
    }
}