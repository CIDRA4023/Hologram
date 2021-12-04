package com.cidra.hologram.widget

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.cidra.hologram.R
import com.cidra.hologram.api.FirebaseService
import com.cidra.hologram.data.WidgetLiveItem

class WidgetRemoteViewFactory (private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {


    private var widgetItem = mutableListOf<WidgetLiveItem>()


    override fun onCreate() {
        widgetItem.clear()

        val sharedPreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        val settingStatus = sharedPreference.getString("setWidgetGroup", "hololive")
        settingStatus?.let { Log.i("widget_preference", it) }
        // リファクタリング必要
        widgetItem = settingStatus?.let { FirebaseService.getWidgetItem(it) }!!

        Log.i("widgetItem", "onCreate")

        // VideoItemを取得して表示させるために待機
        SystemClock.sleep(5000)

    }

    override fun onDataSetChanged() {
        widgetItem.clear()

        val sharedPreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        val settingStatus = sharedPreference.getString("setWidgetGroup", "hololive")
        settingStatus?.let { Log.i("widget_preference", it) }

        // リファクタリング必要
        widgetItem = settingStatus?.let { FirebaseService.getWidgetItem(it) }!!


        // RVideoItemを取得して表示させるために待機
        SystemClock.sleep(5000)

    }

    override fun onDestroy() {
        widgetItem.clear()

    }

    override fun getCount(): Int = widgetItem.size

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(mContext.packageName, R.layout.widget_item_vf)

        try {
            if (widgetItem.isNotEmpty()) {
                Log.i("widgetSize", "${widgetItem.size}")
                Log.i("widgetVideoUrl", widgetItem[position].thumbnail)
                Log.i("widgetPosition", "$position")
                val bitmap = Glide.with(mContext)
                    .asBitmap()
                    .load(widgetItem[position].thumbnail)
                    .submit()
                    .get()
                views.apply {

                    setTextViewText(R.id.widget_vf_title, widgetItem[position].title)
                    setImageViewBitmap(R.id.widget_vf_thumbnail, bitmap)
                    setClickEvent(widgetItem[position].videoId)
                }
            }

            SystemClock.sleep(500)

        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun hasStableIds(): Boolean = true

    private fun RemoteViews.setClickEvent(item: String) {
        setOnClickEvent(item)

    }


    private fun RemoteViews.setOnClickEvent(url: String) {
        // Actionの設定
        val bundle = Bundle().apply {
            putString(WidgetProvider.KEY_VIDEO_URL, url)
        }
        val intent = Intent().apply {
            action = WidgetProvider.ACTION_CLICK_ITEM
            putExtras(bundle)
        }


        setOnClickFillInIntent(R.id.widget_vf_thumbnail, intent)
    }

}