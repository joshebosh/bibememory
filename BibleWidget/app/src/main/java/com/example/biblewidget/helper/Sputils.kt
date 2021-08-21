package com.example.biblewidget.helper

import android.content.Context
import androidx.core.content.edit

class SpUtils {
    companion object {

        private const val FILE_SP_NAME = "FileSp"
        private const val DATA_SP_NAME = "DataSp"
        private const val DATA_STRING_SP_NAME = "DataStringSp"

        fun setFileName(context: Context, fileName: String) {
            val sp = context.getSharedPreferences(FILE_SP_NAME, Context.MODE_PRIVATE)
            sp.edit {
                putString(FILE_SP_NAME, fileName)
            }
        }

        fun getFileName(context: Context): String =
            context.getSharedPreferences(FILE_SP_NAME, Context.MODE_PRIVATE)
                .getString(FILE_SP_NAME, "") ?: ""

        fun saveDataLine(context: Context, id: Int, string: String) {
            val sp = context.getSharedPreferences(DATA_STRING_SP_NAME, Context.MODE_PRIVATE)
            sp.edit {
                putString("Key$id", string)
            }
        }

        fun getDataLine(context: Context, id: Int): String {
            val sp = context.getSharedPreferences(DATA_STRING_SP_NAME, Context.MODE_PRIVATE)
            return sp.getString("Key$id", "") ?: ""
        }

        fun getTotalData(context: Context): Int {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            return sp.getInt("Total", 0)
        }

        fun saveTotalData(context: Context, count: Int) {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            sp.edit {
                putInt("Total", count)
            }
        }

        fun setRotationTime(context: Context, count: Int) {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            sp.edit {
                putInt("Rotation", count)
            }
        }

        fun getRotationTime(context: Context): Int {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            return sp.getInt("Rotation", -1)
        }

        fun setCurrentTime(context: Context, date: Long) {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            sp.edit {
                putLong("Current", date)
            }
        }

        fun getCurrentTime(context: Context): Long {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            return sp.getLong("Current", 0L)
        }

        fun setCurrentID(context: Context, id: Int) {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            sp.edit {
                putInt("ID", id)
            }
        }

        fun getCurrentID(context: Context): Int {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            return sp.getInt("ID", -1)
        }

        //set rotation
        fun setRotationStatus(context: Context, status: Boolean) {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            sp.edit {
                putBoolean("RotationStatus", status)
            }
        }

        fun getRotationStatus(context: Context): Boolean {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            return sp.getBoolean("RotationStatus", false);
        }

        fun setOrderStatus(context: Context, status: Boolean) {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            sp.edit {
                putBoolean("OrderStatus", status)
            }
        }

        fun getOrderStatus(context: Context): Boolean {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            return sp.getBoolean("OrderStatus", false);
        }

        fun setEnabledWidgetStatus(context: Context, status: Boolean) {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            sp.edit {
                putBoolean("EnabledWidgetStatus", status)
            }
        }

        fun getEnabledWidgetStatus(context: Context): Boolean {
            val sp = context.getSharedPreferences(DATA_SP_NAME, Context.MODE_PRIVATE)
            return sp.getBoolean("EnabledWidgetStatus", false);
        }

    }
}