package com.example.biblewidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.example.biblewidget.R
import com.example.biblewidget.helper.SpUtils
import com.example.biblewidget.receiver.MyBroadcastReceiver
import com.example.biblewidget.receiver.MyBroadcastReceiver.Companion.TAG

/**
 * Implementation of App Widget functionality.
 */
class BibleQuotes : AppWidgetProvider() {
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
        SpUtils.setEnabledWidgetStatus(context, true)
        val rot = SpUtils.getRotationTime(context)
        MyBroadcastReceiver.setAlarm(context, rot)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)

        if (context != null) {
            SpUtils.setEnabledWidgetStatus(context, false)
        }
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        SpUtils.setEnabledWidgetStatus(context, false)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

    val id = SpUtils.getCurrentID(context)

    Log.i(TAG, "Current ID: $id")

    var text = SpUtils.getDataLine(context, id)

    if (text.isEmpty()) {
        text = "Please click and update database"
    }

    Log.i(TAG, "Current ID: $text")

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.bible_quotes)
    views.setTextViewText(R.id.appwidget_text, text)

    val action2 = "com.example.biblewidget.Update"

    val refreshIntent = Intent(context, MyBroadcastReceiver::class.java)
    refreshIntent.action = action2
    refreshIntent.putExtra("FromWidget", true)

    //to update app widget manually we need to set getBroadcast()
    val refreshPendingIntent = PendingIntent.getBroadcast(
        context,
        197, refreshIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    views.setOnClickPendingIntent(R.id.base_layout, refreshPendingIntent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}