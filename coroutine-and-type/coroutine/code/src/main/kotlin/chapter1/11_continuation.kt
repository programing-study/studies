package chapter1

import kotlinx.coroutines.delay

class `11_continuation`

suspend fun a() {
    val user = readUser()
    b()
    b()
    b()
    println(user)
}

suspend fun b() {
    for (i in 1..10) {
        c(i)
    }
}

suspend fun c(i: Int) {
    delay(i * 100L)
    println("Tick")
}

suspend fun readUser(): Int {
    return 1
}

suspend fun main() =
    a()

