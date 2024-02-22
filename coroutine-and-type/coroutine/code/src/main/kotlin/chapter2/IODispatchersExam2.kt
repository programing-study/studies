package chapter2

import kotlin.system.measureTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class IODispatchersExam2

suspend fun main() = measureTimeMillis {
    val dispatcher = Dispatchers.IO
        .limitedParallelism(100000)

    coroutineScope {
        repeat(100000) {
            launch(dispatcher) {
                Thread.sleep(1000)
            }
        }
    }
}.let(::println)
// 24277
