package com.cidra.hologram.data

data class LiveItem(
        val videoId: String,
        val title: String,
        val thumbnailUrl: String,
        val startTime: String,
        val currentViewers: String,
        val channelName: String,
        val channelIconUrl: String
)
