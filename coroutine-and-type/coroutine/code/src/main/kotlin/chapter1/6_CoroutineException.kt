package com.study.chapter1

import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CoroutineException

class MyException : Throwable("Just an Exception")

suspend fun main() {
    try {
        suspendCoroutine<Unit> { cont ->
            cont.resumeWithException(MyException())
        }
    } catch (e: MyException) {
        println("Caught!")
    }
}
