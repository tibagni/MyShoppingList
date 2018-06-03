package com.tiagobagni.myshoppinglist

import com.tiagobagni.myshoppinglist.extensions.splitRanges
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test() {
        val items = setOf(1, 2, 3, 4, 7, 8, 9, 24, 25, 26, 30, 32, 40)

        val ranges = items.splitRanges()

        assertEquals(setOf(1 to 4, 7 to 9, 24 to 26, 30 to 30, 32 to 32, 40 to 40), ranges)
    }
}
