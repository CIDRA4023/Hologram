package com.cidra.hologram.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.cidra.hologram.MainActivity
import com.cidra.hologram.api.FirebaseService
import com.cidra.hologram.bindSText
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

//    fun setGroupFilter(sharedPreference: SharedPreferences) {
//        val settingStatus = sharedPreference.getString("setGroup", "hololive")
//        Log.i("settingItem", "$settingStatus")
//        when (settingStatus) {
//            "hololive" -> {
//                _response.value = videoList.filter { it.tagGroup == "holoJp" || it.tagGroup == "holoId" || it.tagGroup == "holoEn" }
//            }
//            "holoStars" -> {
//                _response.value = videoList.filter { it.tagGroup == "holoStars" }
//            }
//            else -> {
//                return
//            }
//        }
//    }

    /**
     * fab並び替え処理
     */
    fun sortByViewer() {

        // 文字列データリストと整数型リストで分割
        // 文字列データリストを先頭にして,視聴者数で降順にソートしたリストと結合
        val notIntList = videoList.filter { it.currentViewers == "premiere" }
        val intList = videoList.filter { it.currentViewers.toIntOrNull() != null }
        val sortedIntList = intList.sortedByDescending { it.currentViewers.toInt() }
        val joinedList = notIntList + sortedIntList

        _response.value = joinedList

    }

    fun sortByStartTime() {
        val sortedVideoList = videoList.sortedByDescending { it.startTime }
        _response.value = sortedVideoList
    }


}