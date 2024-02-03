package chapter2

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class StoppedTerminating

fun main(): Unit = runBlocking {
    try {
        launch {
            delay(1000)
            throw Error("Some Error")
        }
    } catch (e: Throwable) {        // 아무런 도움이 되지 않는다
        println("Will not be printed")
    }

    launch {
        delay(2000)
        println("Will not be printed")
    }
}
/*
Exception in thread "main" java.lang.Error: Some Error
	at chapter2.StoppedTerminatingKt$main$1$1.invokeSuspend(StoppedTerminating.kt:13)
	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
	at kotlinx.coroutines.DispatchedTaskKt.resume(DispatchedTask.kt:235)
	at kotlinx.coroutines.DispatchedTaskKt.dispatch(DispatchedTask.kt:168)
	at kotlinx.coroutines.CancellableContinuationImpl.dispatchResume(CancellableContinuationImpl.kt:474)
	at kotlinx.coroutines.CancellableContinuationImpl.resumeImpl(CancellableContinuationImpl.kt:508)
	at kotlinx.coroutines.CancellableContinuationImpl.resumeImpl$default(CancellableContinuationImpl.kt:497)
	at kotlinx.coroutines.CancellableContinuationImpl.resumeUndispatched(CancellableContinuationImpl.kt:595)
	at kotlinx.coroutines.EventLoopImplBase$DelayedResumeTask.run(EventLoop.common.kt:494)
	at kotlinx.coroutines.EventLoopImplBase.processNextEvent(EventLoop.common.kt:281)
	at kotlinx.coroutines.BlockingCoroutine.joinBlocking(Builders.kt:85)
	at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking(Builders.kt:59)
	at kotlinx.coroutines.BuildersKt.runBlocking(Unknown Source)
	at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking$default(Builders.kt:38)
	at kotlinx.coroutines.BuildersKt.runBlocking$default(Unknown Source)
	at chapter2.StoppedTerminatingKt.main(StoppedTerminating.kt:9)
	at chapter2.StoppedTerminatingKt.main(StoppedTerminating.kt)
 */
