package chapter2

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CoroutineExceptionHandler

fun main(): Unit = runBlocking {
    val handler = CoroutineExceptionHandler { ctx, exception ->
        println("Caught $exception")
    }

    val scope = CoroutineScope(SupervisorJob() + handler) // 조합을 해서 넣을 수 있다.
    // public interface CoroutineExceptionHandler : CoroutineContext.Element { <-- CoroutineContext.Element로 되어 있어서 값만 넘어올 듯한데
    scope.launch {
        delay(1000)
        throw Error("Some error")
    }

    scope.launch {
        delay(2000)
        println("Will be printed")
    }

    delay(3000)
}
/**
Caught java.lang.Error: Some error
Will be printed
 */
