package com.example.biblewidget.work

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.biblewidget.widget.BibleQuotes

class UpdateWork(private val context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {
    override fun doWork(): Result {

        val intent = Intent(context, BibleQuotes::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val manager = AppWidgetManager.getInstance(context)
        val provider = ComponentName(context, BibleQuotes::class.java)
        val ids: IntArray = manager.getAppWidgetIds(provider)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)

        return Result.success()
    }
}