package com.cidra.hologram.data

data class LiveItem(
        val videoId: String,
        val title: String,
        val thumbnailUrl: String,
        val startTime: String,
        val currentViewers: Int,
        val channelName: String,
        val channelIconUrl: String
)
