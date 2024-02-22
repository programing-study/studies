package chapter2

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LaunchIsJob

fun main() = runBlocking {
    val job: Job = launch {
        delay(1000)
        println("Test")
    }
}
