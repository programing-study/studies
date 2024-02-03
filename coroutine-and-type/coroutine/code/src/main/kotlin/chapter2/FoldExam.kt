package chapter2

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job

class FoldExam

fun main() {
    val ctx = CoroutineName("Name1") + Job()

    ctx.fold("") { acc, element -> "$acc$element" }
        .also(::println)        // CoroutineName(Name1)JobImpl{Active}@3b764bce

    val empty = emptyList<CoroutineContext>()
    ctx.fold(empty) { acc, element -> acc + element }
        .joinToString()
        .also(::println)        // CoroutineName(Name1), JobImpl{Active}@3b764bce
}
