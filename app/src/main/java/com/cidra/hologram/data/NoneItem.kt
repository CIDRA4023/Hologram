package com.cidra.hologram.data

data class NoneItem(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val publishedAt: String,
    val viewers: Int,
    val likeCount: Int,
    val channelName: String,
    val channelIconUrl: String,
    val duration: String,
    val tagList: List<String>,
    val tagGroup: String
)
