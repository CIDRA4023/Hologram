package com.cidra.hologram.api

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.cidra.hologram.MainActivity
import com.cidra.hologram.data.LiveItem
import com.cidra.hologram.data.NoneItem
import com.cidra.hologram.data.UpcomingItem
import com.cidra.hologram.utilities.XmlParse
import com.cidra.hologram.utilities.sdf
import com.cidra.hologram.utilities.yesterday
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Channel
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoLiveStreamingDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object YoutubeService {


    private val channelIdMap = mutableMapOf(
            "ときのそら" to "UCp6993wxpyDPHUpavwDFqgg",
            "AZKi" to "UC0TXe_LYZ4scaW2XMyi5_kw",
            "ロボ子" to "UCDqI2jOz0weumE8s7paEk6g",
            "さくらみこ" to "UC-hM6YJuNYVAmUWxeIr9FeA",
            "白上フブキ" to "UCdn5BQ06XqgXoAxIhbqw5Rg",
            "夏色まつり" to "UCQ0UDLQCjY0rmuxCDE38FGg",
            "夜空メル" to "UCD8HOxPs4Xvsm8H0ZxXGiBw",
            "赤井はあと" to "UC1CfXB_kRs3C-zaeTG3oGyg",
            "アキ・ローゼンタール" to "UCFTLzh12_nrtzqBPsTCqenA",
            "湊あくあ" to "UC1opHUrw8rvnsadT-iGp7Cg",
            "癒月ちょこ" to "UCp3tgHXw_HI0QMk1K8qh3gQ",
            "百鬼あやめ" to "UC7fk0CB07ly8oSl0aqKkqFg",
            "紫咲シオン" to "UCXTpFs_3PqI41qX2d9tL2Rw",
            "大空スバル" to "UCvzGlP9oQwU--Y0r9id_jnA",
            "大神ミオ" to "UCp-5t9SrOQwXMU7iIjQfARg",
            "猫又おかゆ" to "UCvaTdHTWBGv3MKj3KVqJVCw",
            "戌神ころね" to "UChAnqc_AY5_I3Px5dig3X1Q",
            "不知火フレア" to "UCvInZx9h3jC2JzsIzoOebWg",
            "白銀ノエル" to "UCdyqAaZDKHXg4Ahi7VENThQ",
            "宝鐘マリン" to "UCCzUftO8KOVkV4wQG1vkUvg",
            "兎田ぺこら" to "UC1DCedRgGHBdm81E1llLhOQ",
            "潤羽るしあ" to "UCl_gCybOJRIgOXw6Qb4qJzQ",
            "星街すいせい" to "UC5CwaMl1eIgY8h02uZw7u8A",
            "天音かなた" to "UCZlDXzGoo7d44bwdNObFacg",
            "桐生ココ" to "UCS9uQI-jC3DE0L4IpXyvr6w",
            "角巻わため" to "UCqm3BQLlJfvkTsX_hvm0UmA",
            "常闇トワ" to "UC1uv2Oq6kNxgATlCiez59hw",
            "姫森ルーナ" to "UCa9Y57gfeY0Zro_noHRVrnw",
            "雪花ラミィ" to "UCFKOVgVbGmX65RxO3EtH3iw",
            "桃鈴ねね" to "UCAWSyEs_Io8MtpY3m-zqILA",
            "獅白ぼたん" to "UCUKD-uaobj9jiqB-VXt71mA",
            "尾丸ポルカ" to "UCK9V2B22uJYu3N7eR_BT9QA",
            "ホロライブ公式" to "UCJFZiqLMntJufDCHc6bQixg"
    )

    private val httpTransport: HttpTransport = NetHttpTransport()

    private val jsonFactory: JsonFactory = JacksonFactory()

    private var youtube: YouTube = YouTube.Builder(httpTransport,
            jsonFactory, null).setApplicationName("").build()


    private val partChannel = listOf("snippet")
    private val partVideo = listOf("snippet", "liveStreamingDetails", "statistics")

    //val field = "items(id,snippet(liveBroadcastContent))"

    val apiKey = getApiKey(MainActivity.instance)

    lateinit var singleChannelItem: Channel

    /**
     * AndroidManifestからapiキーを取得
     */
    private fun getApiKey(context: Context): String {
        val pm = context.packageManager
        var apiKey = ""
        try {
            val packageInfo = pm.getPackageInfo(context.packageName, 0)
            val info = pm.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            apiKey = info.metaData.getString("apiKey").toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return apiKey
    }

    /**
     * チャンネル情報の取得
     * アイコン画像の取得
     */
    private suspend fun getChannelItem(channelID: List<String>): Channel =
            withContext(Dispatchers.IO) {
                val channel = youtube.channels().list(partChannel)
                channel.key = apiKey
                channel.id = channelID
                val response = channel.execute().items
                if (response != null) {
                    val channelItem = response.iterator()
                    while (channelItem.hasNext()) {
                        singleChannelItem = channelItem.next()
                    }
                }
                singleChannelItem
            }

    /**
     * Videoオブジェクトを取得
     * LIVE、配信予定用
     */
    private suspend fun getVideoObject(videoIDs: List<String>, eventType: String) =
            withContext(Dispatchers.IO) {

                var videoObject: Video? = null
                val video = youtube.Videos().list(partVideo)
                video.apply {
                    fields
                    key = apiKey
                    id = videoIDs
                }
                val videoResponse = video.execute().items
                // videoIdListの中でイベントタイプに一致するものだけを抽出
                videoResponse.forEach {
                    if (it.snippet.liveBroadcastContent == eventType) videoObject = it
                }
                videoObject
            }

    /**
     * Videoオブジェクトを取得
     * Archive用
     */
    private suspend fun getVideoObjectList(videoIDs: List<String>) =
            withContext(Dispatchers.IO) {
                val videoObjectList = mutableListOf<Video?>()

                val video = youtube.Videos().list(partVideo)
                video.apply {
                    fields
                    key = apiKey
                    id = videoIDs
                }
                val videoResponse = video.execute().items

                videoResponse.forEach { video ->
                    if (video.snippet.liveBroadcastContent == "none") {
                        // ライブ配信
                        val liveStream: VideoLiveStreamingDetails? = video.liveStreamingDetails
                        liveStream?.let {
                            val actualStartTime = video.liveStreamingDetails.actualStartTime
                            actualStartTime?.let {
                                val startTimeParse = sdf(it)
                                if (startTimeParse.after(yesterday())) videoObjectList.add(video)
                            }
                        } ?: run {
                            // ライブ配信以外の動画
                            val publishedAt = video.snippet.publishedAt
                            val publishedParse = sdf(publishedAt)
                            if (publishedParse.after(yesterday())) videoObjectList.add(video)
                        }
                    }
                }
                videoObjectList
            }

    /**
     * 配信中の動画取得
     */
    suspend fun getLiveItem() =
            withContext(Dispatchers.IO) {
                val videoItemList = mutableListOf<LiveItem>()

                for ((key, value) in channelIdMap) {
                    val videoIdList = XmlParse(value).getVideoIdList()
                    val videoObject = getVideoObject(videoIdList, "live")

                    videoObject?.let {
                        Log.i("videoObject", it.snippet.title)
                        val singleVideo = LiveItem(it.id,
                                it.snippet.thumbnails.high.url,
                                it.snippet.title,
                                getChannelItem(listOf(value)).snippet.thumbnails.high.url,
                                it.liveStreamingDetails.scheduledStartTime.toString(),
                                it.liveStreamingDetails.concurrentViewers.toInt())
                        videoItemList.add(singleVideo)
                    }
                }
                videoItemList.sortBy { it.startTime }
                videoItemList
            }

    /**
     * 配信予定の動画取得
     */
    suspend fun getUpcomingItem() =
            withContext(Dispatchers.IO) {
                val videoItemList = mutableListOf<UpcomingItem>()

                for ((key, value) in channelIdMap) {
                    val videoIdList = XmlParse(value).getVideoIdList()
                    val videoObject = getVideoObject(videoIdList, "upcoming")

                    videoObject?.let {
                        Log.i("videoObject", it.snippet.title)
                        val singleVideo = UpcomingItem(it.id,
                                it.snippet.title,
                                it.snippet.thumbnails.high.url,
                                getChannelItem(listOf(value)).snippet.thumbnails.high.url,
                                it.liveStreamingDetails.scheduledStartTime.toString()
                        )
                        videoItemList.add(singleVideo)
                    }
                }
                videoItemList.sortBy { it.startTime }
                videoItemList
            }

    /**
     * アーカイブの取得
     */
    suspend fun getArchiveItem() =
            withContext(Dispatchers.IO) {
                val videoItemList = mutableListOf<NoneItem>()

                for ((key, value) in channelIdMap) {
                    val videoIdList = XmlParse(value).getVideoIdList()
                    val videoObjectList = getVideoObjectList(videoIdList)
                    videoObjectList.forEach {
                        it?.let {
                            // 評価数非表示動画の対応
                            val singleVideo = it.statistics.likeCount?.toInt()?.let { likeCount ->
                                NoneItem(it.id,
                                        it.snippet.publishedAt.toString(),
                                        it.snippet.thumbnails.high.url,
                                        it.snippet.title,
                                        getChannelItem(listOf(value)).snippet.thumbnails.high.url,
                                        getChannelItem(listOf(value)).snippet.title,
                                        it.statistics.viewCount.toInt(),
                                        likeCount
                                )
                            }
                            if (singleVideo != null) {
                                videoItemList.add(singleVideo)
                            }
                        }
                    }
                }
                videoItemList.sortByDescending { it.publishedAt }
                videoItemList
            }
}