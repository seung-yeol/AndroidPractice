package com.example.presentation

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
    fun aaa(){
        "01053750861".replaceFirst("(^[0-9]{3})([0-9]{4})([0-9]{4})$".toRegex(), "$1 - $2 - $3").let {
            println(it)
        }
    }
}
