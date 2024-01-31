package chapter1


import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

class `10_continuation_decompile`

val COROUTINE_SUSPENDED = 1

fun myfunction(continuation: Continuation<Unit>): Any {
    val continuation = continuation as? MyFunctionContinuation
        ?: MyFunctionContinuation(continuation)
    var counter = continuation.counter

    if (continuation.label == 0) {
        println("Before")
        counter = 0
        continuation.counter = counter
        continuation.label = 1
//        if (delay(1000L, continuation) == COROUTINE_SUSPENDED) { // delay 함수 변경된 듯
//            return COROUTINE_SUSPENDED
//        }
    }
    if (continuation.label == 1) {
        counter = counter + 1
        println("Counter: $counter")
        println("After")
        return Unit
    }
    error("Impossible")
}


class MyFunctionContinuation(
    val completion: Continuation<Unit>
) : Continuation<Unit> {
    override val context: CoroutineContext
        get() = completion.context
    var result: Result<Unit>? = null
    var label = 0
    var counter = 0
    override fun resumeWith(result: Result<Unit>) {
        this.result = result
        val res = try {
            val r = myfunction(this)
            if (r == COROUTINE_SUSPENDED) return
            Result.success(r as Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
        completion.resumeWith(res)
    }
}



