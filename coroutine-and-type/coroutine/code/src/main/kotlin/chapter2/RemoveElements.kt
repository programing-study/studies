package chapter2

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job

class RemoveElements

fun main() {
    val ctx1: CoroutineContext = CoroutineName("Name1") + Job()
    println(ctx1[CoroutineName]?.name)      // Name1
    println(ctx1[Job]?.isActive)            // true

    val ctx2: CoroutineContext = ctx1.minusKey(CoroutineName)
    println(ctx2[CoroutineName]?.name)      // null
    println(ctx2[Job]?.isActive)            // true

    val ctx3 = (ctx1 + CoroutineName("Name2"))
    println(ctx3[CoroutineName]?.name)      // Name2
    println(ctx3[Job]?.isActive)            // true
}
