package com.study.chapter1

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class `8_coroutine_scope`

var continuation2: Continuation<Unit>? = null

suspend fun suspendAndSetContinuation2() {
    suspendCoroutine<Unit> { cont ->
        continuation2 = cont
    }
}

suspend fun main() = coroutineScope {
    println("Before")

    launch {
        delay(1000)
        continuation2?.resume(Unit)
    }
    suspendAndSetContinuation2()
    println("After")
}
