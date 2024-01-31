package chapter1

import kotlinx.coroutines.delay

class `9_continuation`

suspend fun myFunction() {
    println("Before")
    var counter = 0
    delay(1000) // 중단 함수
    counter++
    println("Counter: $counter")
    println("After")
}
