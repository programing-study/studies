package chapter2

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job


fun main() {
    val ctx1: CoroutineContext = CoroutineName("Name1")
    println(ctx1[CoroutineName]?.name)      // Name1
    println(ctx1[Job]?.isActive)            // null

    val ctx2: CoroutineContext = Job()
    println(ctx2[CoroutineName]?.name)      // null
    println(ctx2[Job]?.isActive)            // true

    val ctx3 = ctx1 + ctx2
    println(ctx3[CoroutineName]?.name)      // Name1
    println(ctx3[Job]?.isActive)            // true
}
