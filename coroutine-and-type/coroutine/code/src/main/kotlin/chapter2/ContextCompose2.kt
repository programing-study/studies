package chapter2

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineName


fun main() {
    val ctx1: CoroutineContext = CoroutineName("Name1")
    println(ctx1[CoroutineName]?.name)      // Name1
    val ctx2: CoroutineContext = CoroutineName("Name2")
    println(ctx2[CoroutineName]?.name)      // Name2
    val ctx3 = ctx1 + ctx2
    println(ctx3[CoroutineName]?.name)      // Name2
    val ctx4 = ctx2 + ctx1
    println(ctx4[CoroutineName]?.name)      // Name1
}
