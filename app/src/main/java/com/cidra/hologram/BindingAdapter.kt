package com.cidra.hologram

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
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

    item?.let {
        val dateObject = sdf.parse(it)
        text = when (lang) {
            "ja" -> {
                val sdf2 = SimpleDateFormat("HH時 mm分 開始", Locale.getDefault())
                sdf2.format(dateObject!!).plus(" Started")
            }
            else -> {
                val sdf2 = SimpleDateFormat("HH:mm", Locale.getDefault())
                sdf2.format(dateObject!!).plus(" Started")
            }
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
    val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this.context)

    val lang = Locale.getDefault().language

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
    val sdf12 = SimpleDateFormat("hh:mm \n  a", Locale.getDefault())

    item?.let {
        val dateObject = sdf.parse(it)
        text = when (lang) {
            "ja" -> {
                val timeNotationStatus = sharedPreference.getString("timeNotation", "24")
                Log.i("preference", "$timeNotationStatus")
                if (timeNotationStatus == "12") sdf12.format(dateObject!!) else sdf24.format(dateObject!!)
            }
            else -> {
                val timeNotationStatus = sharedPreference.getString("timeNotation", "12")
                Log.i("preference", "$timeNotationStatus")
                if (timeNotationStatus == "12") sdf12.format(dateObject!!) else sdf24.format(dateObject!!)
            }
        }
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
                "ときのそら" -> it
                "ロボ子" -> it
                "さくらみこ" -> it
                "星街すいせい", "Suisei" -> "星街すいせい"
                "AZKi" -> it
                "夜空メル" -> it
                "アキ・ローゼンタール", "アキロゼ" -> "アキ・ローゼンタール"
                "赤井はあと" -> it
                "白上フブキ" -> it
                "夏色まつり" -> it
                "湊あくあ" -> it
                "紫咲シオン" -> it
                "百鬼あやめ" -> it
                "癒月ちょこ" -> it
                "大空スバル" -> it
                "大神ミオ" -> it
                "猫又おかゆ" -> it
                "戌神ころね" -> it
                "兎田ぺこら" -> it
                "潤羽るしあ" -> it
                "不知火フレア" -> it
                "白銀ノエル" -> it
                "宝鐘マリン" -> it
                "天音かなた" -> it
                "角巻わため" -> it
                "常闇トワ" -> it
                "姫森ルーナ" -> it
                "雪花ラミィ" -> it
                "桃鈴ねね" -> it
                "獅白ぼたん" -> it
                "尾丸ポルカ" -> it
                "Iofi" -> it
                "Moona" -> it
                "Risu" -> it
                "Ollie" -> it
                "Anya" -> it
                "Reine" -> it
                "Calliope" -> it
                "Kiara" -> it
                "Ina'nis" -> it
                "Gura" -> it
                "Amelia" -> it
                "IRyS" -> it
                "Sana" -> it
                "Fauna" -> it
                "Kronii" -> it
                "Mumei" -> it
                "Baelz" -> it
                "花咲みやび" -> it
                "奏手イヅル" -> it
                "アルランディス" -> it
                "律可" -> it
                "アステル" -> it
                "岸堂天真" -> it
                "夕刻ロベル" -> it
                "影山シエン" -> it
                "荒咬オウガ" -> it
                "ホロライブ" -> it
                else -> it
            }
            val chipIcon = when (it) {
                "game" -> R.drawable.ic_outline_videogame_asset_24
                "sing", "cover", "song" -> R.drawable.ic_baseline_music_note_24
                "chat" -> R.drawable.ic_baseline_chat_24
                "birthday" -> R.drawable.ic_baseline_cake_24
                "drawing" -> R.drawable.ic_baseline_draw_24

                "ときのそら" -> R.drawable.ic_sora
                "ロボ子" -> R.drawable.ic_roboko
                "さくらみこ" -> R.drawable.ic_sakuramiko
                "星街すいせい", "Suisei" -> R.drawable.ic_suisei
                "AZKi" -> R.drawable.ic_azuki
                "夜空メル" -> R.drawable.ic_meru
                "アキ・ローゼンタール", "アキロゼ" -> R.drawable.ic_akirose
                "赤井はあと" -> R.drawable.ic_akaihaato
                "白上フブキ" -> R.drawable.ic_fubuki
                "夏色まつり" -> R.drawable.ic_matsuri
                "湊あくあ" -> R.drawable.ic_aqua
                "紫咲シオン" -> R.drawable.ic_shion
                "百鬼あやめ" -> R.drawable.ic_ayame
                "癒月ちょこ" -> R.drawable.ic_choko
                "大空スバル" -> R.drawable.ic_subaru
                "大神ミオ" -> R.drawable.ic_mio
                "猫又おかゆ" -> R.drawable.ic_okayu
                "戌神ころね" -> R.drawable.ic_korone
                "兎田ぺこら" -> R.drawable.ic_pekora
                "潤羽るしあ" -> R.drawable.ic_rushia
                "不知火フレア" -> R.drawable.ic_flare
                "白銀ノエル" -> R.drawable.ic_fubuki
                "宝鐘マリン" -> R.drawable.ic_houshoumarin
                "天音かなた" -> R.drawable.ic_amanekanata
                "角巻わため" -> R.drawable.ic_watame
                "常闇トワ" -> R.drawable.ic_towa
                "姫森ルーナ" -> R.drawable.ic_luna
                "雪花ラミィ" -> R.drawable.ic_ramy
                "桃鈴ねね" -> R.drawable.ic_nene
                "獅白ぼたん" -> R.drawable.ic_botan
                "尾丸ポルカ" -> R.drawable.ic_poruka
                "IOFI", "Iofi" -> R.drawable.ic_iofi
                "Moona", "ムーナ" -> R.drawable.ic_moona
                "Risu" -> R.drawable.ic_risu
                "Ollie" -> R.drawable.ic_ollie
                "Anya" -> R.drawable.ic_anya
                "Reine" -> R.drawable.ic_reine
                "Calliope" -> R.drawable.ic_calliope
                "Kiara" -> R.drawable.ic_takanashikiara
                "Ina'nis" -> R.drawable.ic_inanis
                "Gura" -> R.drawable.ic_gura
                "Amelia" -> R.drawable.ic_ameliawatson
                "IRyS" -> R.drawable.ic_irys
                "Sana" -> R.drawable.ic_sana
                "Fauna" -> R.drawable.ic_fauna
                "Kronii" -> R.drawable.ic_kronii
                "Mumei" -> R.drawable.ic_mumei
                "Baelz" -> R.drawable.ic_baelz
                "花咲みやび" -> R.drawable.ic_miyabi
                "奏手イヅル" -> R.drawable.ic_izuru
                "アルランディス" -> R.drawable.ic_arurandeisu
                "律可" -> R.drawable.ic_rikka
                "アステル" -> R.drawable.ic_astel
                "岸堂天真" -> R.drawable.ic_temma_1
                "夕刻ロベル" -> R.drawable.ic_roberu
                "影山シエン" -> R.drawable.ic_shien
                "荒咬オウガ" -> R.drawable.ic_oga
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

