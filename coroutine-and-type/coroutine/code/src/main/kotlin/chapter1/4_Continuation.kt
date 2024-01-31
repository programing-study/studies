package com.study.chapter1

import kotlin.coroutines.suspendCoroutine

class `Continuation-1`

suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { continuation ->
        println("Before too")
    }
    println("Before")
}
