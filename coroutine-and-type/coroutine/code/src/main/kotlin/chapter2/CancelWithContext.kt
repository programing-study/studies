package chapter2

import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CancelWithContext

suspend fun main() = coroutineScope {
    val job = Job()
    launch(job) {
        try {
            delay(2000)
            println("Job is done")
        } finally {
            println("Finally")
            withContext(NonCancellable) {
                delay(1000)
                println("clean up")
            }
        }
    }
    delay(1000)
    job.cancelAndJoin()
    println("Cancel done")
}
/*
Finally
clean up
Cancel done
 */
