package com.example.presentation

import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Test

import org.junit.Assert.*
import java.util.*

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
    fun scan() {
        /*Observable.just(1).scan(0) { acc: Int, new: Int ->
            println(" acc = $acc   new : $new")
            new
        }.subscribe {
            println("it : $it")
        }*/

        Observable.empty<Int>().scan(123) { acc: Int, new: Int ->
            println(" acc = $acc   new : $new")
            new
        }.subscribe {
            println("it : $it")
        }
    }

    @Test
    fun aaa() {
        "01053750861".replaceFirst("(^[0-9]{3})([0-9]{4})([0-9]{4})$".toRegex(), "$1 - $2 - $3").let {
            println(it)
        }
    }

    @Test
    fun aaaa() {
        val sources = "동해물과 백두산이 마르고 \n닳도록 하느님이 보우하사 우리나라만세".split("\\s").toTypedArray()

        println(sources.joinToString { it + " / " })
    }

    @Test
    fun aaaaa() {
    }
}
