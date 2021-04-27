package com.example.test

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
    fun all() {
        val integers = listOf<Int>()

        integers.all {
            it == 10
        }.let {
            println("all : $it")
        }
    }

    @Test
    fun any() {
        val integers = listOf<Int>()

        integers.any {
            it == 10
        }.let {
            println("any : $it")
        }
    }
}