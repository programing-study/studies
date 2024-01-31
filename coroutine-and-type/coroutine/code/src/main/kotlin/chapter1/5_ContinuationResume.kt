package com.study.chapter1

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ContinuationResume

suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { continuation ->
        continuation.resume(Unit)
    }
    println("After")
}
