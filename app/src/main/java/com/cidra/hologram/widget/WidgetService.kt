package com.cidra.hologram.widget

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        WidgetRemoteViewFactory(this.applicationContext)
}