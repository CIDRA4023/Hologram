package com.cidra.hologram.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.cidra.hologram.R

/**
 * Implementation of App Widget functionality.
 */
class WidgetProvider : AppWidgetProvider() {

    companion object {

        const val KEY_VIDEO_URL = "key_shot_url"

        const val ACTION_CLICK_ITEM = "CLICK_ITEM"
        const val ACTION_NEXT = "NEXT"
        const val ACTION_BACK = "BACK"
        const val ACTION_UPDATE = "UPDATE"

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        val intent = Intent(context, WidgetService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

        RemoteViews(context.packageName, R.layout.widget_layout).apply {
            setRemoteAdapter(R.id.widget_view_flipper, Intent(context, WidgetService::class.java))
            setEmptyView(R.id.widget_view_flipper, R.id.widget_empty_view)
        }.let {
            val clickIntent = Intent(context, WidgetProvider::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val clickPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                clickIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            it.setPendingIntentTemplate(R.id.widget_view_flipper, clickPendingIntent)


            // ボタンのクリックリスナー実装
            val nextIntent = Intent(context, WidgetProvider::class.java).apply {
                action = ACTION_NEXT
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val nextPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                nextIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            it.setOnClickPendingIntent(R.id.widget_button_next, nextPendingIntent)

            val backIntent = Intent(context, WidgetProvider::class.java).apply {
                action = ACTION_BACK
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val backPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                backIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            it.setOnClickPendingIntent(R.id.widget_button_back, backPendingIntent)

            val updateIntent = Intent(context, WidgetProvider::class.java).apply {
                action = ACTION_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val updatePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                updateIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            it.setOnClickPendingIntent(R.id.widget_button_update, updatePendingIntent)


            appWidgetManager.updateAppWidget(appWidgetId, it)
        }


//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)


        when (intent.action) {
            ACTION_NEXT -> {
                Log.i("click", "next")
                val rv = RemoteViews(context.packageName, R.layout.widget_layout)
                rv.showNext(R.id.widget_view_flipper)

                AppWidgetManager.getInstance(context).updateAppWidget(
                    intent.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID
                    ), rv
                )
            }
            ACTION_BACK -> {
                val rv = RemoteViews(context.packageName, R.layout.widget_layout)
                rv.showPrevious(R.id.widget_view_flipper)

                AppWidgetManager.getInstance(context).updateAppWidget(
                    intent.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID
                    ), rv
                )
            }
            ACTION_UPDATE -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetComponentName =
                    ComponentName(context.applicationContext, WidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(appWidgetComponentName)
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_view_flipper)
            }
            ACTION_CLICK_ITEM -> {
                val baseUrl = "https://www.youtube.com/watch?v="
                val videoUrl = intent.getStringExtra(KEY_VIDEO_URL)
                Log.i("click", "$videoUrl")
                val uri = Uri.parse(baseUrl + videoUrl)
                Intent(Intent.ACTION_VIEW, uri).let {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(it)
                }
            }

        }
    }
}