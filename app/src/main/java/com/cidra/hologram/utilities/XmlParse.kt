package com.cidra.hologram.utilities

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL


/**
 * Xmlのパース
 * 各チャンネルのRSSフィードからVideoIdを取得
 */
class XmlParse(private val channelID: String) {

    private val GET_VIDEO_ID_COUNT = 3
    private var count = 0

    suspend fun getVideoIdList(): List<String> =
            withContext(Dispatchers.IO) {
                val videoParseID = mutableListOf<String>()
                // 各チャンネルのRSSフィードのUrlデータ作成
                val url = URL("https://www.youtube.com/feeds/videos.xml?channel_id=$channelID")


                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()
                parser.setInput(url.openStream(), null)
                // タグのタイプを判別する変数
                var eventType = parser.eventType

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.name == "yt:videoId") {
                            // Xmlのテキストコンテンツのみ取得する処理
                            if (parser.next() == XmlPullParser.TEXT) {
                                count++
                                val videoId = parser.text
                                videoParseID.add(videoId)
                                Log.i("videoXml", videoId.toString())
                            }
                        }
                    }
                    eventType = parser.next()
                    if (count == GET_VIDEO_ID_COUNT) break
                }
                videoParseID
            }
}