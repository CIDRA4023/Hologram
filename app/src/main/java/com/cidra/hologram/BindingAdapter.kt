package com.cidra.hologram

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cidra.hologram.adapters.LiveListAdapter
import com.cidra.hologram.adapters.NoneListAdapter
import com.cidra.hologram.data.LiveItem
import com.cidra.hologram.data.NoneItem
import com.cidra.hologram.viewmodels.NetworkStatus
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
            text = sdf2.format(dateObject!!).plus( " Started")
        }
    } else {
        val sdf2 = SimpleDateFormat("HH時 mm分 開始", Locale.getDefault())
        item?.let {
            val dateObject = sdf.parse(it)
            text = sdf2.format(dateObject!!)
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
            0, 1, 2, 3 -> cviewerText.text = count.toString().plus(cviewerText.resources.getString(R.string.current_viewer_count_unit))
            4,5,6 -> cviewerText.text = (ceil(count.toDouble() / 1000)).toInt().toString().plus(cviewerText.resources.getString(R.string.current_viewer_count_unit_K))
            7,8,9 -> cviewerText.text = (ceil(count.toDouble() / 100000) / 10).toString().plus(cviewerText.resources.getString(R.string.current_viewer_count_unit_M))
            else -> cviewerText.text = cviewerText.resources.getString(R.string.current_viewer_premium)
        }
    } else {
        when (count.length) {
            0, 1, 2, 3, 4 -> cviewerText.text = count.plus(" 人視聴中")
            5, 6 -> cviewerText.text = (ceil(count.toDouble() / 1000) / 10).toString().plus("万 人視聴中")
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
            0, 1, 2, 3 -> viewerText.text = count.toString().plus(viewerText.resources.getString(R.string.view_count_unit))
            4,5,6 -> viewerText.text = (ceil(count.toDouble() / 1000)).toInt().toString().plus(viewerText.resources.getString(R.string.view_count_unit_K))
            else -> viewerText.text = (ceil(count.toDouble() / 100000) / 10).toString().plus(viewerText.resources.getString(R.string.view_count_unit_M))
        }
    } else {
        when (count.toString().length) {
            0, 1, 2, 3, 4 -> viewerText.text =  count.toString().plus(" 回再生")
            else -> { viewerText.text = (ceil(count.toDouble() / 1000) / 10).toString().plus("万 回再生") }
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

