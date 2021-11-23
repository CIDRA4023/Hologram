package com.cidra.hologram.data

data class LiveItem(
        val videoId: String,
        val title: String,
        val thumbnailUrl: String,
        val startTime: String,
        val currentViewers: String,
        val channelName: String,
        val channelIconUrl: String,
        val tagList: List<String>,
        val tagGroup: String
)

data class WidgetLiveItem(
        val videoId: String,
        val title: String,
        val thumbnail: String
)
