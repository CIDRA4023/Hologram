package com.cidra.hologram.data

data class NoneItem(
    val videoID: String,
    val publishedAt: String,
    val thumbnailUrl: String,
    val title: String,
    val iconUrl: String,
    val channelName: String,
    val viewers: Int,
    val likeCount: Int
)
