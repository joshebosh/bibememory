package com.example.biblewidget

import org.joda.time.DateTime
import org.joda.time.Seconds
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val time = DateTime.now().minusSeconds(100)
        val rotation = 10
        val status = checkForTime(time.toDate().time, rotation)
        assertEquals(true, status)
    }


    fun checkForTime(time: Long, rot: Int): Boolean {
        val now = DateTime.now()
        val pre = DateTime(time)

        val dif = Seconds.secondsBetween(pre, now).seconds

        return dif > rot
    }
}
