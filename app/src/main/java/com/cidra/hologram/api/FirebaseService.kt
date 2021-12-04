package com.cidra.hologram.api

import android.util.Log
import com.cidra.hologram.data.LiveItem
import com.cidra.hologram.data.NoneItem
import com.cidra.hologram.data.UpcomingItem
import com.cidra.hologram.data.WidgetLiveItem
import com.cidra.hologram.utilities.sdf
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object FirebaseService {
    private val database = Firebase.database
    private val ref = database.getReference("/video")


    suspend fun getLiveItem(): MutableList<LiveItem> {
        val videoItemList = mutableListOf<LiveItem>()
        return suspendCoroutine { continuation ->
            val videoItems = ref.orderByChild("eventType").equalTo("live").get()
            videoItems.addOnSuccessListener { dataSnapshot ->
                dataSnapshot.children.forEach {

                    val tagList = listOf(it.child("tag").child("category").value.toString()) +
                            it.child("tag").child("member").value.toString().split(",")
                    val tagGroup = it.child("tag").child("group").value.toString()
                    Log.i("videoId_live", it.key.toString())

                    /**
                     * 通常のライブ配信：そのまま表示
                     * プレミアム公開：０
                     */
                    val currentViewers = when (it.child("likeCount").value.toString()) {
                        "プレミアム公開中" -> 99999999 // 並び替え時に先頭表示かつcurrentViewerで分岐するため
                        else -> it.child("currentViewers").value.toString()
                    }.toString()

                    val singleItem = LiveItem(
                        it.key.toString(),
                        it.child("title").value.toString(),
                        it.child("thumbnailUrl").value.toString(),
                        it.child("startTime").value.toString(),
                        currentViewers,
                        it.child("channelName").value.toString(),
                        it.child("channelIconUrl").value.toString(),
                        tagList,
                        tagGroup
                    )
                    videoItemList.add(singleItem)
                    Log.i("widgetItem", "${videoItemList.size}")
                }
                videoItemList.sortByDescending { it.startTime }
                continuation.resume(videoItemList)
            }.addOnFailureListener {
                Log.e("firebase_live", "${it.message}")
            }
        }
    }


    suspend fun getUpcomingItem(): MutableList<UpcomingItem> {
        return suspendCoroutine { continuation ->
            val videoItemList = mutableListOf<UpcomingItem>()


            val videoItems = ref.orderByChild("eventType").equalTo("upcoming").get()
            videoItems.addOnSuccessListener { dataSnapshot ->
                dataSnapshot.children.forEach {

                    val tagList = listOf(it.child("tag").child("category").value.toString()) +
                            it.child("tag").child("member").value.toString().split(",")
                    val tagGroup = it.child("tag").child("group").value.toString()

                    Log.i("TAGC", "${it.child("title").value}")
                    Log.i("TAGC", "${ it.child("tag").child("category").value }")
                    val singleItem = UpcomingItem(
                        it.key.toString(),
                        it.child("title").value.toString(),
                        it.child("thumbnailUrl").value.toString(),
                        it.child("scheduledStartTime").value.toString(),
                        it.child("channelName").value.toString(),
                        it.child("channelIconUrl").value.toString(),
                        tagList,
                        tagGroup
                    )

                    videoItemList.add(singleItem)
                }
                videoItemList.sortBy { it.scheduledStartTime }
                continuation.resume(videoItemList)
            }.addOnFailureListener {
                Log.e("firebase_upcoming", "${it.message}")
            }
        }
    }


    suspend fun getNoneItem(): MutableList<NoneItem> {
        return suspendCoroutine { continuation ->
            val videoItemList = mutableListOf<NoneItem>()

            val videoItems = ref.orderByChild("eventType").equalTo("none").get()
            videoItems.addOnSuccessListener { dataSnapshot ->
                dataSnapshot.children.forEach {
                    Log.i("TAG", "${it.child("title").value}")
                    val publishedAt = sdf(it.child("publishedAt").value.toString())

                    val tagList = listOf(it.child("tag").child("category").value.toString()) +
                            it.child("tag").child("member").value.toString().split(",")
                    val tagGroup = it.child("tag").child("group").value.toString()

                    /**
                     * 評価非表示：０
                     * 評価表示：そのまま表示
                     */
                    val likeCount = when (it.child("likeCount").value.toString()) {
                        "None" -> 0
                        else -> it.child("likeCount").value.toString().toInt()
                    }
                    val singleItem = NoneItem(
                        it.key.toString(),
                        it.child("title").value.toString(),
                        it.child("thumbnailUrl").value.toString(),
                        it.child("publishedAt").value.toString(),
                        it.child("viewCount").value.toString().toInt(),
                        likeCount,
                        it.child("channelName").value.toString(),
                        it.child("channelIconUrl").value.toString(),
                        it.child("duration").value.toString(),
                        tagList,
                        tagGroup
                    )
                    videoItemList.add(singleItem)
                }
                // アップロード順に並び替え
                videoItemList.sortByDescending { it.publishedAt }

                continuation.resume(videoItemList)
            }.addOnFailureListener {
                Log.e("firebase_none", "${it.message}")
            }
        }
    }


    fun getWidgetItem(setting: String): ArrayList<WidgetLiveItem> {
        val liveItems = ref.orderByChild("eventType").equalTo("live").get()
        val videoItems = arrayListOf<WidgetLiveItem>()

        liveItems.addOnSuccessListener { dataSnapshot ->
            dataSnapshot.children.forEach {
                var tagGroup = it.child("tag").child("group").value.toString()
                if (tagGroup == "holoJp" || tagGroup == "holoEn"|| tagGroup == "holoId") { tagGroup = "hololive"}
                val videoItem = WidgetLiveItem(videoId = it.key.toString(),
                    title = it.child("title").value.toString(),
                    thumbnail = it.child("thumbnailUrl").value.toString(),
                    tagGroup = tagGroup)

                Log.i("widgetThumbnailUrl", "${it.child("thumbnailUrlWidget").value.toString()}")

                if (videoItem.tagGroup == setting) {
                    videoItems.add(videoItem)
                }

                Log.i("widgetItemSnapshot", "${videoItems.size}")
            }
        }.addOnFailureListener {
            Log.e("getVideoItem", "${it.message}")
        }
        return videoItems
    }


}
