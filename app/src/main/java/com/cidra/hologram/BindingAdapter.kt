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
    val sharedPreference = PreferenceManager.getDefaultSharedPreferences(recyclerView.context)
    val settingStatus = sharedPreference.getString("setGroup", "hololive")
    Log.i("setting_pre_binding", "$settingStatus")
    val filteredData = when (settingStatus) {
        "hololive" -> {
            data?.filter { it.tagGroup == "holoJp" || it.tagGroup == "holoId" || it.tagGroup == "holoEn" }
        }

        "holoStars" -> {
            data?.filter { it.tagGroup == "holoStars" }
        }

        else -> {
            data
        }
    }
    val adapter = recyclerView.adapter as LiveListAdapter
    adapter.submitList(filteredData)
}

//@BindingAdapter("UpcomingListData")
//fun bindURecyclerView(recyclerView: RecyclerView, data: List<UpcomingItem>?) {
//    val sharedPreference = PreferenceManager.getDefaultSharedPreferences(recyclerView.context)
//    val settingStatus = sharedPreference.getString("setGroup", "hololive")
//    Log.i("setting_pre_binding", "$settingStatus")
//    val filteredData = when (settingStatus) {
//        "hololive" -> {
//            data?.filter { it.tagGroup == "holoJp" || it.tagGroup == "holoId" || it.tagGroup == "holoEn" }
////            todayData?.filter {
////                it.today.tagGroup == "holoJp" || it.today.tagGroup == "holoId" || it.today.tagGroup == "holoEn"
////            }
////            tomorrowData?.filter {
////                it.tomorrow.tagGroup == "holoJp" || it.tomorrow.tagGroup == "holoId" || it.tomorrow.tagGroup == "holoEn"
////            }
//        }
//        "holoStars" -> {
//            data?.filter { it.tagGroup == "holoStars" }
////            todayData?.filter {
////                it.today.tagGroup == "holoStars"
////            }
////            tomorrowData?.filter {
////                it.tomorrow.tagGroup == "holoStars"
////            }
//        }
//        else -> {
//            return
//        }
//    }
//    val adapter = recyclerView.adapter as UpcomingListAdapter
//    adapter.submitList(filteredData)
//}

@BindingAdapter("ArchivesListData")
fun bindARecyclerView(recyclerView: RecyclerView, data: List<NoneItem>?) {
    val sharedPreference = PreferenceManager.getDefaultSharedPreferences(recyclerView.context)
    val settingStatus = sharedPreference.getString("setGroup", "hololive")
    Log.i("setting_pre_binding", "$settingStatus")
    val filteredData = when (settingStatus) {
        "hololive" -> {
            data?.filter { it.tagGroup == "holoJp" || it.tagGroup == "holoId" || it.tagGroup == "holoEn" }
        }

        "holoStars" -> {
            data?.filter { it.tagGroup == "holoStars" }
        }

        else -> {
            data
        }
    }
//    val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager(recyclerView.context).orientation)
//    recyclerView.addItemDecoration(dividerItemDecoration)
    val adapter = recyclerView.adapter as NoneListAdapter
    adapter.submitList(filteredData)
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

    relativeText.setReferenceTime(dateObject)

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
                if (timeNotationStatus == "12") sdf12.format(dateObject!!) else sdf24.format(
                    dateObject!!
                )
            }

            else -> {
                val timeNotationStatus = sharedPreference.getString("timeNotation", "12")
                Log.i("preference", "$timeNotationStatus")
                if (timeNotationStatus == "12") sdf12.format(dateObject!!) else sdf24.format(
                    dateObject!!
                )
            }
        }
    }

}

@BindingAdapter("currentViewerFormat")
fun currentViewerFormat(cviewerText: TextView, count: String) {
    val lang = Locale.getDefault().language
    // currentViewersに格納されている値が整数か文字列かで分岐
    return if (count.toIntOrNull() != null) {
        // 言語によって表記を変更
        if (lang != "ja") {
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
                else -> cviewerText.text =
                    cviewerText.resources.getString(R.string.current_viewer_premium)
            }
        }
        // 文字列（プレミアム公開）だったとき
    } else {
        cviewerText.text = cviewerText.resources.getString(R.string.current_viewer_premium)
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
            statusTextView.text = statusTextView.resources.getString(R.string.empty_item_live)
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
                "membership" -> if (lang != "ja") "Membership" else "メン限"
                "asmr" -> "ASMR"

                "ときのそら" -> if (lang != "ja") "Sora" else it
                "ロボ子" -> if (lang != "ja") "Robocosan" else it
                "さくらみこ" -> if (lang != "ja") "Miko" else it
                "星街すいせい", "Suisei" -> if (lang != "ja") "Suisei" else it
                "AZKi" -> it
                "夜空メル" -> if (lang != "ja") "Yozora Mel" else it
                "アキ・ローゼンタール", "アキロゼ" -> if (lang != "ja") "Aki Rosenthal" else "アキ・ローゼンタール"
                "赤井はあと" -> if (lang != "ja") "Haachama" else it
                "白上フブキ" -> if (lang != "ja") "Fubuki" else it
                "夏色まつり" -> if (lang != "ja") "Matsuri" else it
                "湊あくあ" -> if (lang != "ja") "Aqua" else it
                "紫咲シオン" -> if (lang != "ja") "Shion" else it
                "百鬼あやめ" -> if (lang != "ja") "Ayame" else it
                "癒月ちょこ" -> if (lang != "ja") "Choco" else it
                "大空スバル" -> if (lang != "ja") "Subaru" else it
                "大神ミオ" -> if (lang != "ja") "Mio" else it
                "猫又おかゆ" -> if (lang != "ja") "Okayu" else it
                "戌神ころね" -> if (lang != "ja") "Korone" else it
                "兎田ぺこら" -> if (lang != "ja") "Pekora" else it
                "潤羽るしあ" -> if (lang != "ja") "Rushia" else it
                "不知火フレア" -> if (lang != "ja") "Flare" else it
                "白銀ノエル" -> if (lang != "ja") "Noel" else it
                "宝鐘マリン" -> if (lang != "ja") "Marine" else it
                "天音かなた" -> if (lang != "ja") "Kanata" else it
                "角巻わため" -> if (lang != "ja") "Watame" else it
                "常闇トワ" -> if (lang != "ja") "Towa" else it
                "姫森ルーナ" -> if (lang != "ja") "Luna" else it
                "雪花ラミィ" -> if (lang != "ja") "Lamy" else it
                "桃鈴ねね" -> if (lang != "ja") "Nene" else it
                "獅白ぼたん" -> if (lang != "ja") "Botan" else it
                "尾丸ポルカ" -> if (lang != "ja") "Polka" else it
                "ラプラス・ダークネス" -> if (lang != "ja") "Laplus " else it
                "鷹嶺ルイ" -> if (lang != "ja") "Lui" else it
                "博衣こより" -> if (lang != "ja") "Koyori" else it
                "沙花叉クロヱ" -> if (lang != "ja") "Chloe" else it
                "風真いろは" -> if (lang != "ja") "Iroha" else it
                "火威青" -> if(lang != "ja") "Ao" else it
                "音乃瀬奏" -> if(lang != "ja") "Kanade" else it
                "一条莉々華" -> if(lang != "ja") "Rika" else it
                "儒烏風亭らでん" -> if(lang != "ja") "Raden" else it
                "轟はじめ" -> if(lang != "ja") "Hajime" else it
                "Iofi" -> it
                "Moona" -> it
                "Risu" -> it
                "Ollie" -> it
                "Anya" -> it
                "Reine" -> it
                "Kobo" -> it
                "Kaela" -> it
                "Zeta" -> it
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
                "Shiori" -> it
                "Kosiki" -> it
                "Nerissa" -> it
                "FUWAMOCO" -> it
                "花咲みやび" -> if (lang != "ja") "Miyabi" else it
                "奏手イヅル" -> if (lang != "ja") "Izuru" else it
                "アルランディス" -> if (lang != "ja") "Arurandeisu" else it
                "律可" -> if (lang != "ja") "Rikka" else it
                "アステル" -> if (lang != "ja") "Lada" else it
                "岸堂天真" -> if (lang != "ja") "Temma" else it
                "夕刻ロベル" -> if (lang != "ja") "Roberu" else it
                "影山シエン" -> if (lang != "ja") "Shien" else it
                "荒咬オウガ" -> if (lang != "ja") "Oga" else it
                "夜十神封魔" -> if (lang != "ja") "Fuma" else it
                "羽継烏有" -> if (lang != "ja") "Uyu" else it
                "緋崎ガンマ " -> if (lang != "ja") "Gamma" else it
                "水無世燐央" -> if (lang != "ja") "Rio" else it
                "Altare" -> it
                "Dezmond" -> it
                "Axel" -> it
                "Vesper" -> it
                "Bettel" -> it
                "Flayon" -> it
                "Hakka" -> it
                "Josuiji" -> it
                "" -> it
                "hololive ホロライブ " -> "Hololive JP"
                "holostars" -> "HOLOSTARS JP"
                "hololive Indonesia" -> "Hololive ID"
                "hololive English" -> "Hololive EN"
                "holostars English" -> "HOLOSTARS EN"
                else -> it
            }
            val chipIcon = when (it) {
                "game" -> R.drawable.ic_baseline_videogame_asset_24
                "sing", "cover", "song" -> R.drawable.ic_baseline_music_note_24
                "chat" -> R.drawable.ic_baseline_chat_24
                "birthday" -> R.drawable.ic_baseline_cake_24
                "drawing" -> R.drawable.ic_baseline_draw_24
                "watchAlong" -> R.drawable.ic_baseline_ondemand_video_24
                "asmr", "ASMR" -> R.drawable.ic_baseline_headphones_24
                "membership" -> R.drawable.ic_baseline_workspace_premium

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
                "白銀ノエル" -> R.drawable.ic_noel
                "宝鐘マリン" -> R.drawable.ic_houshoumarin
                "天音かなた" -> R.drawable.ic_amanekanata
                "角巻わため" -> R.drawable.ic_watame
                "常闇トワ" -> R.drawable.ic_towa
                "姫森ルーナ" -> R.drawable.ic_luna
                "雪花ラミィ" -> R.drawable.ic_ramy
                "桃鈴ねね" -> R.drawable.ic_nene
                "獅白ぼたん" -> R.drawable.ic_botan
                "尾丸ポルカ" -> R.drawable.ic_poruka
                "ラプラス・ダークネス" -> R.drawable.ic_laplusdarknesss_1
                "鷹嶺ルイ" -> R.drawable.ic_takanelui
                "博衣こより" -> R.drawable.ic_hakuikoyori
                "沙花叉クロヱ" -> R.drawable.ic_sakamatakuroe
                "風真いろは" -> R.drawable.ic_kazamairohach
                "火威青" -> R.drawable.ic_hiodoshiao
                "音乃瀬奏" -> R.drawable.ic_otonosekanade1
                "一条莉々華" -> R.drawable.ic_ichijouririka
                "儒烏風亭らでん" -> R.drawable.ic_juufuuteiraden
                "轟はじめ" -> R.drawable.ic_todoroki_hajime1
                "IOFI", "Iofi" -> R.drawable.ic_iofi
                "Moona", "ムーナ" -> R.drawable.ic_moona
                "Risu" -> R.drawable.ic_risu
                "Ollie" -> R.drawable.ic_ollie
                "Anya" -> R.drawable.ic_anya
                "Reine" -> R.drawable.ic_reine
                "Kobo" -> R.drawable.ic_kobo
                "Kaela" -> R.drawable.ic_kaela
                "Zeta" -> R.drawable.ic_zeta
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
                "Shiori" -> R.drawable.ic_shiori
                "Koseki" -> R.drawable.ic_koseki
                "Nerissa" -> R.drawable.ic_nerissa
                "FUWAMOCO" -> R.drawable.ic_fuwamoco
                "花咲みやび" -> R.drawable.ic_miyabi
                "奏手イヅル" -> R.drawable.ic_izuru
                "アルランディス" -> R.drawable.ic_arurandeisu
                "律可" -> R.drawable.ic_rikka
                "アステル" -> R.drawable.ic_astel
                "岸堂天真" -> R.drawable.ic_temma_1
                "夕刻ロベル" -> R.drawable.ic_roberu
                "影山シエン" -> R.drawable.ic_shien
                "荒咬オウガ" -> R.drawable.ic_oga
                "夜十神封魔" -> R.drawable.ic_fuma
                "羽継烏有" -> R.drawable.ic_uyu
                "緋崎ガンマ" -> R.drawable.ic_gamma
                "水無世燐央" -> R.drawable.ic_rio
                "Altare" -> R.drawable.ic_altare
                "Dezmond" -> R.drawable.ic_dezmond
                "Axel" -> R.drawable.ic_axel
                "Vesper" -> R.drawable.ic_vesper
                "Bettel" -> R.drawable.ic_bettel
                "Flayon" -> R.drawable.ic_flayon
                "Hakka" -> R.drawable.ic_hakka
                "Josuiji" -> R.drawable.ic_josuiji

                "hololive ホロライブ ", "hololive Indonesia", "holostars English",
                "hololive English", "holostars" -> R.drawable.ic_baseline_verified_24
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

