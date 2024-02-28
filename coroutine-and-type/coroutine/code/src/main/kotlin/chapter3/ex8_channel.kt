package chapter3

import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ex8_channel

suspend fun sendString(
    channel: SendChannel<String>,
    text: String,
    time: Long
) {
    while (true) {
        delay(time)
        channel.send(text)
    }
}

fun main() = runBlocking {
    val channel = Channel<String>()
    launch { sendString(channel, "foo", 200L) }
    launch { sendString(channel, "BAR!", 500L) }
    repeat(50) {
        println(channel.receive())
    }
    coroutineContext.cancelChildren()
}
/*
foo
foo
BAR!
foo
foo
BAR!
foo
foo
foo
BAR!
foo
foo
BAR!
foo
foo
foo
BAR!
foo
foo
BAR!
foo
foo
foo
BAR!
foo
foo
BAR!
foo
foo
foo
BAR!
foo
foo
BAR!
foo
foo
foo
BAR!
foo
foo
BAR!
foo
foo
foo
BAR!
foo
foo
BAR!
foo
foo
 */
