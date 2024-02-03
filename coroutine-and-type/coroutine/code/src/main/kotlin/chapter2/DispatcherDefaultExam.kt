package chapter2

import kotlin.random.Random
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DispatcherDefaultExam

suspend fun main() = coroutineScope {
    repeat(1000) {
        launch {
            List(1000) { Random.nextLong() }.maxOrNull()
            val threadName = Thread.currentThread().name
            println("Running on thread : $threadName")
        }
    }
}
