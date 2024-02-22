package chapter2

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class CoroutineScopeExam

fun main() = runBlocking {
    val a = coroutineScope {
        delay(1000)
        10
    }// 기다리고,
    println("a is calculated")  // 실행
    val b = coroutineScope {
        delay(3000)
        20
    }

    println(a)
    println(b)
}
