package chapter3

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ex2_channel

suspend fun main(): Unit = coroutineScope {
    val channel = Channel<Int>()
    launch {
        repeat(5) { index ->
            delay(1000)
            println("Producing next one")
            channel.send(index * 2)
        }
        channel.close()
    }

    launch {
        for (element in channel) {
            println(element)
        }
//         또는
//        channel.consumeEach { element ->
//            println(element)
//        }
    }
}

/*
Producing next one
0
Producing next one
2
Producing next one
4
Producing next one
6
Producing next one
8
 */
