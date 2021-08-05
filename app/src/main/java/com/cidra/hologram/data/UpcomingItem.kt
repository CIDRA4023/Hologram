package com.cidra.hologram.data

data class UpcomingItem(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val scheduledStartTime: String,
    val channelName: String,
    val channelIconUrl: String,
    val tagList: List<String>
)
