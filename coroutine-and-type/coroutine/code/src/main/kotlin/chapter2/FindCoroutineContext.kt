package chapter2

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job

class FindCoroutineContext

fun main() {
    val ctx: CoroutineContext = CoroutineName("A name")
    val coroutineName: CoroutineName? = ctx[CoroutineName]
    // 또는 ctx.get(CoroutineName)
    println(coroutineName?.name)    // A name
    val job: Job? = ctx[Job]
    println(job)                    // null
}
