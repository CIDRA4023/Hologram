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

@BindingAdapter("timeFormat")
fun TextView.bindLText(item: String?) {
    item?.let {
        val today = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val sdf2 = SimpleDateFormat("HH時mm分")
        val sdf3 = SimpleDateFormat("dd日HH時mm分")
        val dateObject = sdf.parse(item)

        text = sdf2.format(dateObject)

    }
}

@BindingAdapter("scheduleFormat")
fun TextView.bindSText(item: String?) {
    item?.let {
        val today = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val sdf2 = SimpleDateFormat("HH:mm")
        val dateObject = sdf.parse(item)

        text = sdf2.format(dateObject)

    }
}

@BindingAdapter("currentViewerFormat")
fun TextView.currentViewerFormat(count: String) {
    return when (count.length) {
        0, 1, 2, 3, 4 -> text = count.plus(" 人視聴中")
        5, 6 -> text = (ceil(count.toDouble() / 1000) / 10).toString().plus("万 人視聴中")
        else -> text = count
    }
}

@BindingAdapter("viewerFormat")
fun TextView.viewerFormat(count: Int) {
    return when (count.toString().length) {
        0, 1, 2, 3, 4 -> text = count.toString().plus(" 回再生")
        else -> text = (ceil(count.toDouble() / 1000) / 10).toString().plus("万 回再生")
    }
}


/**
 *
 */
@BindingAdapter("YoutubeStatus")
fun bindStatus(statusImageView: ImageView, status: NetworkStatus?) {
    when (status) {
        NetworkStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        NetworkStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        NetworkStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}

