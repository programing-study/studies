package chapter2

import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SuspendAndCoroutineContext

suspend fun printName() {
    println(coroutineContext[CoroutineName]?.name)
}

suspend fun main() = withContext(CoroutineName("Outer")) {
    printName()     // Outer

    launch(CoroutineName("Inner")) {
        printName()  // Inner
    }

    delay(10)
    printName()       // Outer
}
