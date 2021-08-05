package com.cidra.hologram

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cidra.hologram.adapters.LiveListAdapter
import com.cidra.hologram.adapters.NoneListAdapter
import com.cidra.hologram.data.LiveItem
import com.cidra.hologram.data.NoneItem
import com.cidra.hologram.databinding.LayoutChipBinding
import com.cidra.hologram.viewmodels.NetworkStatus
import com.github.curioustechizen.ago.RelativeTimeTextView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

@BindingAdapter("imgUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {

    Glide.with(imgView.context)
        .load(imgUrl)
        .apply(RequestOptions().placeholder(R.drawable.loading_animation))
        .diskCacheStrategy(DiskCacheStrategy.NONE)  // 前回読み込んだ画像キャッシュを削除するのに必要
        .skipMemoryCache(true)
        .into(imgView)
}

/**
 * Archivesのサムネイル読み込み用
 * 配信済み動画のサムネイルは頻繁に変更されることはないため
 * Glideにてキャッシュを保存しておく
 */
@BindingAdapter("imgAUrl")
fun bindArchiveImage(imgView: ImageView, imgUrl: String?) {

    Glide.with(imgView.context)
        .load(imgUrl)
        .apply(RequestOptions().placeholder(R.drawable.loading_animation))
        .into(imgView)
}

@BindingAdapter("iconUrl")
fun bindIconImage(imgView: ImageView, imgUrl: String?) {

    Glide.with(imgView.context)
        .load(imgUrl)
        .diskCacheStrategy(DiskCacheStrategy.NONE)  // 前回読み込んだ画像キャッシュを削除するのに必要
        .skipMemoryCache(true)
        .circleCrop()
        .into(imgView)
}

@BindingAdapter("liveListData")
fun bindLRecyclerView(recyclerView: RecyclerView, data: List<LiveItem>?) {
    val adapter = recyclerView.adapter as LiveListAdapter
    adapter.submitList(data)
}

@BindingAdapter("ArchivesListData")
fun bindARecyclerView(recyclerView: RecyclerView, data: List<NoneItem>?) {
    val adapter = recyclerView.adapter as NoneListAdapter
    adapter.submitList(data)
}


@BindingAdapter("liveStartTimeFormat")
fun TextView.bindLText(item: String?) {
    val lang = Locale.getDefault().language

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    if (lang != "ja") {
        val sdf2 = SimpleDateFormat("HH:mm", Locale.getDefault())
        item?.let {
            val dateObject = sdf.parse(it)
            text = sdf2.format(dateObject!!).plus(" Started")
        }
    } else {
        val sdf2 = SimpleDateFormat("HH時 mm分 開始", Locale.getDefault())
        item?.let {
            val dateObject = sdf.parse(it)
            text = sdf2.format(dateObject!!)
        }
    }
}


@BindingAdapter("relativeTime")
fun bindRelativeText(relativeText: RelativeTimeTextView, time: String?) {

    val lang = Locale.getDefault().language
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val dateObject = sdf.parse(time).time

    val now = Date().time
    val relativeTime = now - dateObject

    relativeText.setReferenceTime(now - relativeTime)

}

@BindingAdapter("videoDuration")
fun bindDurationText(durationText: TextView, duration: String?) {
    // 再生時間のパターン
    val sdfDurationSS = SimpleDateFormat("'PT'ss'S'", Locale.getDefault())
    val sdfDurationMM = SimpleDateFormat("'PT'mm'M'", Locale.getDefault())
    val sdfDurationMMSS = SimpleDateFormat("'PT'mm'M'ss'S'", Locale.getDefault())
    val sdfDurationHH = SimpleDateFormat("'PT'HH'H'", Locale.getDefault())
    val sdfDurationHHSS = SimpleDateFormat("'PT'HH'H'ss'S'", Locale.getDefault())
    val sdfDurationHHMM = SimpleDateFormat("'PT'HH'H'mm'M'", Locale.getDefault())
    val sdfDurationHHMMSS = SimpleDateFormat("'PT'HH'H'mm'M'ss'S'", Locale.getDefault())

    // 表示再生時間
    val setTextMMSS = SimpleDateFormat("mm:ss", Locale.getDefault())
    val setTextHHMMSS = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    Log.i("duration", duration!!)
    duration?.let {
        val isMatchedH = Regex("H").containsMatchIn(it)
        val isMatchedM = Regex("M").containsMatchIn(it)
        val isMatchedS = Regex("S").containsMatchIn(it)

        /**
         * 再生時間で条件分岐
         */
        when {
            // 1時間以上の再生時間があるとき
            isMatchedH -> {
                Log.i("duration", "HMS")
                if (isMatchedM && isMatchedS) {
                    val durationObject = sdfDurationHHMMSS.parse(it)
                    durationText.text = setTextHHMMSS.format(durationObject!!)
                } else if (isMatchedH && isMatchedM) {
                    val durationObject = sdfDurationHHMM.parse(it)
                    durationText.text = setTextHHMMSS.format(durationObject!!)
                } else if (isMatchedH && isMatchedS) {
                    val durationObject = sdfDurationHHSS.parse(it)
                    durationText.text = setTextHHMMSS.format(durationObject!!)
                } else {
                    val durationObject = sdfDurationHH.parse(it)
                    durationText.text = setTextHHMMSS.format(durationObject!!)
                }
            }
            // 1時間未満で1分以上再生時間があるとき
            isMatchedM -> {
                Log.i("duration", "MS")
                if (isMatchedM && isMatchedS) {
                    val durationObject = sdfDurationMMSS.parse(it)
                    durationText.text = setTextMMSS.format(durationObject!!)
                } else {
                    val durationObject = sdfDurationMM.parse(it)
                    durationText.text = setTextMMSS.format(durationObject!!)
                }
            }
            // 1分未満で1秒以上再生時間があるとき
            isMatchedS -> {
                Log.i("duration", "S")
                val durationObject = sdfDurationSS.parse(it)
                durationText.text = setTextMMSS.format(durationObject!!)
            }

        }
    }
}


@BindingAdapter("scheduleStartTimeFormat")
fun TextView.bindSText(item: String?) {

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val sdf2 = SimpleDateFormat("HH:mm", Locale.getDefault())

    item?.let {
        val dateObject = sdf.parse(it)
        text = sdf2.format(dateObject!!)
    }

}

@BindingAdapter("currentViewerFormat")
fun currentViewerFormat(cviewerText: TextView, count: String) {
    val lang = Locale.getDefault().language
    return if (lang != "ja") {
        when (count.length) {
            0, 1, 2, 3 -> cviewerText.text = count.toString()
                .plus(cviewerText.resources.getString(R.string.current_viewer_count_unit))
            4, 5, 6 -> cviewerText.text = (ceil(count.toDouble() / 1000)).toInt().toString()
                .plus(cviewerText.resources.getString(R.string.current_viewer_count_unit_K))
            // 視聴者数100万人以上はめったにないので暫定的にコメントアウト
            // 理想はプレミアム公開とライブ配信で条件分岐できるようフラグを付与する
//            7,8,9 -> cviewerText.text = (ceil(count.toDouble() / 100000) / 10).toString().plus(cviewerText.resources.getString(R.string.current_viewer_count_unit_M))
            else -> cviewerText.text =
                cviewerText.resources.getString(R.string.current_viewer_premium)
        }
    } else {
        when (count.length) {
            0, 1, 2, 3, 4 -> cviewerText.text = count.plus(" 人視聴中")
            5, 6 -> cviewerText.text =
                (ceil(count.toDouble() / 1000) / 10).toString().plus("万 人視聴中")
            // プレミアム公開だった時
            else -> cviewerText.text = count
        }
    }
}

@BindingAdapter("viewCountFormat")
fun viewerFormat(viewerText: TextView, count: Int) {
    val lang = Locale.getDefault().language
    Log.i("lang", lang)

    return if (lang != "ja") {
        when (count.toString().length) {
            0, 1, 2, 3 -> viewerText.text =
                count.toString().plus(viewerText.resources.getString(R.string.view_count_unit))
            4, 5, 6 -> viewerText.text = (ceil(count.toDouble() / 1000)).toInt().toString()
                .plus(viewerText.resources.getString(R.string.view_count_unit_K))
            else -> viewerText.text = (ceil(count.toDouble() / 100000) / 10).toString()
                .plus(viewerText.resources.getString(R.string.view_count_unit_M))
        }
    } else {
        when (count.toString().length) {
            0, 1, 2, 3, 4 -> viewerText.text = count.toString().plus(" 回再生")
            else -> {
                viewerText.text = (ceil(count.toDouble() / 1000) / 10).toString().plus("万 回再生")
            }
        }
    }
}


/**
 *
 */
@BindingAdapter("LoadingStatus")
fun bindStatusImage(statusImageView: ImageView, status: NetworkStatus?) {
    when (status) {
        NetworkStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        NetworkStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        NetworkStatus.DONE, NetworkStatus.NONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("LoadingStatusText")
fun bindStatusText(statusTextView: TextView, status: NetworkStatus?) {
    when (status) {
        NetworkStatus.NONE -> {
            statusTextView.text = statusTextView.resources.getString(R.string.none_item_live)
        }
        NetworkStatus.DONE -> {
            statusTextView.visibility = View.GONE
        }
    }
}

@BindingAdapter("addChip")
fun bindAddChip(parent: ViewGroup, tag: List<String>) {
    val lang = Locale.getDefault().language

    // このメソッドが呼ばれるたびにチップを削除する
    parent.removeAllViews()

    tag.forEach {
        if (it != "null") {
            val bindingChip = DataBindingUtil.inflate<LayoutChipBinding>(
                LayoutInflater.from(parent.context),
                R.layout.layout_chip,
                parent,
                false
            )

            val chipText = when (it) {
                "game" -> if (lang != "ja") "Game" else "ゲーム"
                "sing" -> if (lang != "ja") "Sing" else "歌枠"
                "live" -> if (lang != "ja") "LIVE" else "LIVE"
                "chat" -> if (lang != "ja") "Chat" else "雑談"
                "birthday" -> if (lang != "ja") "Birthday" else "生誕祭"
                "song" -> if (lang != "ja") "Original Song" else "オリジナル曲"
                "drawing" -> if (lang != "ja") "Drawing" else "お絵かき"
                "watchAlong" -> if (lang != "ja") "Watch Along" else "同時視聴"
                "cover" -> if (lang != "ja") "Cover" else "歌ってみた"
                "ときのそら" -> "\uD83D\uDC3B $it"
                "ロボ子" -> "\t\uD83E\uDD16 $it"
                "さくらみこ" -> "\uD83C\uDF38 $it"
                "星街すいせい" -> "☄️ $it"
                "AZKi" -> "⚒️ $it"
                "夜空メル" -> "\uD83C\uDF1F $it"
                "アキ・ローゼンタール" -> "\uD83C\uDF4E $it"
                "赤井はあと" -> "❤️ $it"
                "白上フブキ" -> "\uD83C\uDF3D $it"
                "夏色まつり" -> "\uD83C\uDFEE $it"
                "湊あくあ" -> "⚓️ $it"
                "紫咲シオン" -> "\uD83C\uDF19 $it"
                "百鬼あやめ" -> "\uD83D\uDE08 $it"
                "癒月ちょこ" -> "\uD83D\uDC8B $it"
                "大空スバル" -> "\uD83D\uDE91 $it"
                "大神ミオ" -> "\uD83C\uDF32 $it"
                "猫又おかゆ" -> "\uD83C\uDF59 $it"
                "戌神ころね" -> "\uD83E\uDD50 $it"
                "兎田ぺこら" -> "\uD83D\uDC6F\u200D♀️ $it"
                "潤羽るしあ" -> "\uD83E\uDD8B $it"
                "不知火フレア" -> "\uD83D\uDD25 $it"
                "白銀ノエル" -> "⚔️ $it"
                "宝鐘マリン" -> "\uD83C\uDFF4\u200D $it"
                "天音かなた" -> "\uD83D\uDCAB $it"
                "角巻わため" -> "\uD83D\uDC0F $it"
                "常闇トワ" -> "\uD83D\uDC7E $it"
                "姫森ルーナ" -> "\uD83C\uDF6C $it"
                "雪花ラミィ" -> "☃️ $it"
                "桃鈴ねね" -> "\uD83E\uDD5F $it"
                "獅白ぼたん" -> "♌️ $it"
                "尾丸ポルカ" -> "\uD83C\uDFAA $it"
                "IOFI" -> "\uD83C\uDFA8 $it"
                "MOONA", "ムーナ" -> "\uD83D\uDD2E Moona"
                "Risu" -> "\uD83D\uDC3F $it"
                "Ollie" -> "\uD83E\uDDDF\u200D♀️ $it"
                "Anya" -> "\uD83C\uDF42 $it"
                "Reine" -> "\uD83E\uDD9A $it"
                "Calliope" -> "\uD83D\uDC80 $it"
                "Kiara" -> "\uD83D\uDC14 $it"
                "Ina'nis" -> "\uD83D\uDC19 $it"
                "Gura" -> "\uD83D\uDD31 $it"
                "Amelia" -> "\uD83D\uDD0E $it"
                "IRyS" -> "\uD83D\uDC8E $it"
                else -> it
            }
            val chipIcon = when (it) {
                "game" -> R.drawable.ic_outline_videogame_asset_24
                "sing", "cover", "song" -> R.drawable.ic_baseline_music_note_24
                "chat" -> R.drawable.ic_baseline_chat_24
                "birthday" -> R.drawable.ic_baseline_cake_24
                "drawing" -> R.drawable.ic_baseline_draw_24
                else -> null
            }
            chipIcon?.let { icon ->
                bindingChip.chipTag.apply {
                    chipStartPadding = resources.getDimension(R.dimen.chipPadding)
                    setChipIconResource(icon)
                }
            }
            bindingChip.chipTag.text = chipText

            parent.addView(bindingChip.root)
        }
    }

}


