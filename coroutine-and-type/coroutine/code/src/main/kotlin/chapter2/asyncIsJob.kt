package chapter2

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class asyncIsJob

fun main() = runBlocking {
    val deferred: Deferred<String> = async {
        delay(1000)
        "Test"
    }
    val job: Job = deferred
    println(job)
    deferred.await()
    println(job)
}

/**
DeferredCoroutine{Active}@77f03bb1
DeferredCoroutine{Completed}@77f03bb1
 */
