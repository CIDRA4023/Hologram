package com.cidra.hologram.data

data class LiveItem(
        val videoID: String,
        val thumbnailUrl: String,
        val title: String,
        val iconUrl: String,
        val startTime: String,
        val viewers: Int
)
