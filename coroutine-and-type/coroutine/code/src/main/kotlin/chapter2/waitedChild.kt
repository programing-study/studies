package chapter2

import kotlinx.coroutines.NonCancellable.children
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class waitedChild

fun main() = runBlocking {
    val job1 = launch {
        delay(1000)
        println("Test1")
    }
    val job2 = launch {
        delay(2000)
        println("Test2")
    }

    job1.join()
    job2.join()
    println("All tests are done")

    val childrenNum = children.count()
    println("Number of children : $childrenNum")
    children.forEach { it.join() }
    println("All tests are done")
}

/*
Test1
Test2
All tests are done
Number of children : 0
All tests are done
 */
