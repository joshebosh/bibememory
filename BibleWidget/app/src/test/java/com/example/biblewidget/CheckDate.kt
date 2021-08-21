package com.example.biblewidget

import org.joda.time.DateTime
import org.joda.time.Seconds

fun main() {
    val time = DateTime.now().minusSeconds(100)
    val rotation = 10
    println(checkForTime(time.toDate().time, rotation))
}

fun checkForTime(time: Long, rot: Int): Boolean {
    val now = DateTime.now()
    val pre = DateTime(time)

    val dif = Seconds.secondsBetween(now, pre).seconds

    return dif <= rot
}