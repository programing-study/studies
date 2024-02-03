package chapter2

import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DispatchersWithVirtualThread

suspend fun main() = measureTimeMillis {
    coroutineScope {
        repeat(100000) {
            launch(Dispatchers.Loom) {
                Thread.sleep(1000)
            }
        }
    }
}.let(::println)
// 1860

object LoomDispatcher : ExecutorCoroutineDispatcher() {
    override val executor = Executor { command ->
        Thread.startVirtualThread(command)
    }

    override fun close() {
        error("Cannot be invoked on Dispatchers.LOOM")
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        executor.execute(block)
    }
}

val Dispatchers.Loom: CoroutineDispatcher
    get() = LoomDispatcher
