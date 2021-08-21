package com.example.biblewidget.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.biblewidget.helper.SpUtils
import com.example.biblewidget.widget.BibleQuotes
import com.example.biblewidget.work.UpdateWork
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MyBroadcastReceiver : BroadcastReceiver() {

    private val action = "com.example.biblewidget.UpdateBibleWidget"
    private val action2 = "com.example.biblewidget.Update"

    override fun onReceive(context: Context, intent: Intent?) {

        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            val time = SpUtils.getRotationTime(context)
            setAlarm(context, time)
//            setWork(context, time)
            Log.i(TAG, "All code executed")
        } else if (intent?.action == action) {
            val stopRotationStatus = SpUtils.getRotationStatus(context)

            if (stopRotationStatus) {
                cancelAlarm(context)
                return
            }

            val time = SpUtils.getRotationTime(context)
            setAlarm(context, time)
            updateNumber(context)
            updateWidget(context)
//            setWork(context, time)
//            Toast.makeText(context, "Updating", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "All code executed")
        } else if (intent?.action == action2) {
            val time = SpUtils.getRotationTime(context)
            setAlarm(context, time)
            updateNumber(context)
            updateWidget(context)
            Log.i(TAG, "All code executed")
        }
    }

    private fun updateNumber(ctx: Context) {
        val orderStatus = SpUtils.getOrderStatus(ctx)
        val id = SpUtils.getCurrentID(ctx)
        val total = SpUtils.getTotalData(ctx)
        Log.i(TAG, "Order status: $orderStatus")
        Log.i(TAG, "Current ID: $id")
        Log.i(TAG, "Total: $total")

        if (orderStatus) {
            val newID = Random.nextInt(0, total-1)
            Log.i(TAG, "Current ID: $newID")
            SpUtils.setCurrentID(ctx, newID)

        } else {
            when {
                id < total-1 -> {
                    SpUtils.setCurrentID(ctx, id + 1)
                    Log.i(TAG, "Current ID: ${id + 1}")
                }
                id == total-1 -> {
                    SpUtils.setCurrentID(ctx, 0)
                    Log.i(TAG, "Current ID: 0")
                }
                else -> {
                    SpUtils.setCurrentID(ctx, 0)
                    Log.i(TAG, "Current ID: 0")
                }
            }
        }
    }

    private fun updateWidget(ctx: Context) {
        val intent = Intent(ctx, BibleQuotes::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val manager = AppWidgetManager.getInstance(ctx)
        val provider = ComponentName(ctx, BibleQuotes::class.java)
        val ids: IntArray = manager.getAppWidgetIds(provider)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        ctx.sendBroadcast(intent)
        Log.i(TAG, "Request for update widgets")
    }

    companion object {

        private const val action = "com.example.biblewidget.UpdateBibleWidget"
        private const val requestedID = 10254548;
        const val TAG = "MY TAG";

        fun cancelAlarm(ctx: Context) {
            val alarm =
                ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarm.cancel(getPendingIntent(ctx))
            Log.i(TAG, "Alarm canceled")
        }

        fun setAlarm(ctx: Context, time: Int) {
            cancelAlarm(ctx)

            if (time <= 0) {
                return
            }

            val widget = SpUtils.getEnabledWidgetStatus(ctx)
            if (!widget) {
                return
            }

            val intent = Intent(ctx, MyBroadcastReceiver::class.java)
            intent.action = action
            intent.flags = Intent.FLAG_RECEIVER_FOREGROUND

            val pendingIntent = PendingIntent.getBroadcast(
                ctx, requestedID, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val SDK_INT = Build.VERSION.SDK_INT

            val alarmTime = System.currentTimeMillis() + (time * 1000L)

//            if (SDK_INT >= Build.VERSION_CODES.M) {
//                alarmManager.setExactAndAllowWhileIdle(
//                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    alarmTime,
//                    pendingIntent
//                )
//            } else {
//                alarmManager.setExact(
//                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    alarmTime,
//                    pendingIntent
//                )
//            }
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(alarmTime, pendingIntent),
                pendingIntent
            )

            Log.i(TAG, "Set alarm after $time")
        }

        private fun getPendingIntent(ctx: Context): PendingIntent? {
            val alarmIntent = Intent(ctx, MyBroadcastReceiver::class.java)
            alarmIntent.action = action
            return PendingIntent.getBroadcast(
                ctx,
                requestedID,
                alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }

        fun cancelWork(context: Context) {
            val manager = WorkManager.getInstance(context)
            manager.cancelAllWork()
        }

        fun setWork(context: Context, time: Int) {

            val manager = WorkManager.getInstance(context)

            val periodicSyncDataWork =
                PeriodicWorkRequest.Builder(
                    UpdateWork::class.java,
                    time * 1000L,
                    TimeUnit.MILLISECONDS
                )
                    .addTag("Update widgets")
                    .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS
                    )
                    .build()

            manager.enqueueUniquePeriodicWork(
                "UpdateWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicSyncDataWork
            )
        }
    }
}