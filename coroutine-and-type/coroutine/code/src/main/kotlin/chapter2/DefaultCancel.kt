package chapter2

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DefaultCancel

suspend fun main() = coroutineScope {
    val job = launch {
        repeat(1000) { i ->
            delay(200)
            println("Printing $i")
        }
    }

    delay(1100)
    job.cancel()
    job.join()
    println("Cancelled successfully")
}
/**
Printing 0
Printing 1
Printing 2
Printing 3
Printing 4
Cancelled successfully
 */
