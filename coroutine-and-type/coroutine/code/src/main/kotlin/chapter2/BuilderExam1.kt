package chapter2

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LaunchExample

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    GlobalScope.launch {
        delay(1000L)
        println("world!")
    }
    GlobalScope.launch {
        delay(1000L)
        println("world!")
    }
    GlobalScope.launch {
        delay(1000L)
        println("world!")
    }
    println("hello")
    Thread.sleep(2000L)
}
