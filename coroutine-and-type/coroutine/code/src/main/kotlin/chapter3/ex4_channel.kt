package chapter3

import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class ex4_channel

suspend fun main(): Unit = coroutineScope {
    val channel = produce(capacity = 3) {
        repeat(5) { index ->
            send(index * 5)
            delay(100)
            println("Sent")
        }
    }

    delay(1000)
    for (element in channel) {
        println(element)
        delay(1000)
    }
}

/*
Sent
Sent
Sent
0
Sent
5
Sent
10
15
20
 */
