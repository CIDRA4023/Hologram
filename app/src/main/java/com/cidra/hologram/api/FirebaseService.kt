package com.cidra.hologram.api

import android.util.Log
import com.cidra.hologram.data.LiveItem
import com.cidra.hologram.data.NoneItem
import com.cidra.hologram.data.UpcomingItem
import com.cidra.hologram.utilities.sdf
import com.cidra.hologram.utilities.yesterday
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
                    Log.i("TAG", "${it.child("title").value}")
                    Log.i("TAG", it.key.toString())
                    Log.i("TAG", "${it.child("currentViewers").value}")
                    val singleItem = LiveItem(
                        it.key.toString(),
                        it.child("title").value.toString(),
                        it.child("thumbnailUrl").value.toString(),
                        it.child("startTime").value.toString(),
                        it.child("currentViewers").value.toString(),
                        it.child("channelName").value.toString(),
                        it.child("channelIconUrl").value.toString()
                    )
                    videoItemList.add(singleItem)
                }
                videoItemList.sortBy { it.startTime }
                continuation.resume(videoItemList)
            }.addOnFailureListener {
                Log.e("TAG", "Error getting data", it)
            }
        }
    }


    suspend fun getUpcomingItem(): MutableList<UpcomingItem> {
        return suspendCoroutine { continuation ->
            val videoItemList = mutableListOf<UpcomingItem>()

            val videoItems = ref.orderByChild("eventType").equalTo("upcoming").get()
            videoItems.addOnSuccessListener { dataSnapshot ->
                dataSnapshot.children.forEach {
                    Log.i("TAG", "${it.child("title").value}")
                    val singleItem = UpcomingItem(
                        it.key.toString(),
                        it.child("title").value.toString(),
                        it.child("thumbnailUrl").value.toString(),
                        it.child("scheduledStartTime").value.toString(),
                        it.child("channelName").value.toString(),
                        it.child("channelIconUrl").value.toString()
                    )

                    videoItemList.add(singleItem)
                }
                videoItemList.sortBy { it.scheduledStartTime }
                continuation.resume(videoItemList)
            }.addOnFailureListener {
                Log.e("TAG", "Error getting data", it)
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

                    val singleItem = NoneItem(
                        it.key.toString(),
                        it.child("title").value.toString(),
                        it.child("thumbnailUrl").value.toString(),
                        it.child("publishedAt").value.toString(),
                        it.child("viewCount").value.toString().toInt(),
                        it.child("likeCount").value.toString().toInt(),
                        it.child("channelName").value.toString(),
                        it.child("channelIconUrl").value.toString()
                    )
                    if (publishedAt.after(yesterday())) {
                        videoItemList.add(singleItem)
                    }
                }
                videoItemList.sortByDescending { it.publishedAt }
                continuation.resume(videoItemList)
            }.addOnFailureListener {
                Log.e("TAG", "Error getting data", it)
            }
        }
    }

}
