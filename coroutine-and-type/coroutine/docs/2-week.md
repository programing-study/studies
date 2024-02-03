# 2장 코틀린 코루틴 라이브러리

## 코루틴 빌더

Kotlinx.coroutines 라이브러리가 제공하는 세가지 필수적인 코루틴 빌더들

* launch
* runBlocking
* async

## launch 빌더

launch 동작 방식은 thread 함수를 호출해서 새로운 스레드를 시작하는 것과 비슷함.

```kotlin
fun main() {
    GlobalScope.launch {
        delay(1000L)
        println("world!")
    }
    GlobalScope.launch {
        delay(1000L)
        println("world!")
    }
    GlobalScope.launch {
        delay(1000L)
        println("world!")
    }
    println("hello")
    Thread.sleep(2000L)
}

//hello
//world!
//world!
//world!
```

launch 함수는 CoroutineScope 인터페이스의 확장함수이다.
CoroutineScope interface는 부모 쿠루틴과 자식 코루틴 사이의 관계를 정ㄹㅂ하기 위한 목적으로 `구조화된 동시성(structured concurrency)`의 핵심이다.

launch는 데몬스레드와 어느정도 비슷하지만 훨씬 가볍다.
`demon thread는 백그라운드에서 동작하며, 우선순위가 낮은 스레드이다. launch와 비교한 이유는 둘다 프로그램이 끝나는 걸 막을 수 없어서이다.`

## runBlocking 빌더

코루틴이 스레드를 블로킹하지 않고 작업을 중단시키기만 하는 것이 일반적인 법칙이지만, 블로킹이 필요한 경우가 있다.  
메인 함수의 경우 프로그램을 너무 빨리 끝내지 않기 위해 스레드를 블로킹해야 하는데, 이럴 때 runBlocking을 사용한다.

코루틴이 중단되었을 경우 runBlocking 빌더는 중단 메인 함수와 마찬가지로 시작한 스레드를 중단시킨다.
runBlocking 내부에서 delay(1000L)을 호출하면 `Thread.sleep(1000L)`과 비슷하게 동작한다.

runBlocking이 사용되는 특수한 경우는 실제로 두 가지

1. 프로그램이 끝나는 걸 방지하기 위해 스레드를 블로킹할 필요가 있는 메인 함수
2. 스레드를 블로킹할 필요가 있는 유닛 테스트

`main 함수`는 `runBlocking` 대신에 `suspend`를 붙여 중단함수를 만드는 방법을 주로 사용함.

## async 빌더

* `async 코루틴 빌더`는 `launch`와 비슷하지만 값을 생성하도록 설계되어 있음.  
  값은 람다 표현식에 의해 반환되어야 함.
* `async 함수`는 `Deferred<T>` 타입의 객체를 리턴하며, 여기서 `T`는 생성되는 값 타입
* `Deferred작업`이 끝나면 값을 반환하는 중단 메서드인 `await`가 있음

`async빌더`와 `launch 빌더`와의 차이점은 `async 빌더`는 값을 반환함
`async`는 값을 생성할 때, `launch`는 값이 필요하지 않을때 사용함

## 구조화된 동시성

코루틴 GlobalScope에서 시작되었다면 프로그램은 해당 코루틴을 기다리지 않음.  
코루틴은 어떤 스레드도 블록하지 않기 때문에 프로그램이 끝나는 것을 막을 방법이 없음.

``` kotlin
fun main() = runBlocking {
    this.launch {
        delay(1000L)
        println("world! 1")
    }
    launch {
        delay(2000L)
        println("world! 2")
    }
    println("hello,")
}

// hello,
// world! 1
// world! 2

```

runBlocking 내부에 launch 를 호출하면 launch는 runBlocking의 자식이 된다.

부모는 자식들을 위한 스코프를 제공하고 자식들을 해당 스코프 내에서 호출함.  
이것을 `구조화된 동시성` 이라고 함

* 자식은 부모로부터 컨텍스트를 상속받는다.
* 부모는 모든 자식이 작업을 마칠 때까지 기다린다.
* 부모 코루틴이 취소되면 자식 코루틴되 취소된다.
* 자식 코루틴에서 에러가 발생하면, 부모 코루틴 또한 에러로 소멸한다.

`runBlocking` 은 자식이 될 수 없으며 루트 코루틴으로만 사용된다.

## coroutineScope 사용하기

coroutineScope는 중단 함수 내에서 스코프가 필요할 때 일반적으로 사용하는 함수

동시성처리를 하기 위해서는 함수를 coroutineScope로 래핑한 다음, 스코프 내에서 빌더를 사용해야 함.  
모든 것은 스코프 내에서 빌더를 호출함으로써 시작됨.

# 코루틴 컨텍스트

코루틴 빌더의 정의를 보면 첫 번째 파라미터가 CoroutineContext라는 사실을 알 수 있다.

``` kotlin
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val newContext = newCoroutineContext(context)
    val coroutine = if (start.isLazy)
        LazyStandaloneCoroutine(newContext, block) else
        StandaloneCoroutine(newContext, active = true)
    coroutine.start(start, coroutine, block)
    return coroutine
}

```

CoroutineContext를 감싸는 래퍼(wrapper)처럼 보인다.

## CoroutineContext 인터페이스

CoroutineContext는 원소나 원소들의 집합을 나타내는 인터페이스이다.

## CoroutineContext에서 원소 찾기

컨텍스트에서 모든 원소는 식별할 수 있는 유일한 Key를 가지고 있다.

```kotlin
fun main() {
    val ctx: CoroutineContext = CoroutineName("A name")
    val coroutineName: CoroutineName = ctx[CoroutineName]
    // 또는 ctx.get(CoroutineName)
    println(coroutineName?.name)    // A name
    val job: Job = ctx[Job]
    println(job)                    // null
}
```

CoroutineName은 타입이나 클래스가 아닌 companion 객체이다.

## 컨텍스트 더하기

- CoroutineContext의 유용한 기능은 두 개의 CoroutineContext를 합쳐 하나의 CoroutineContext를 만드는 것.
- 다른 키를 가진 두 우ㅝㄴ소를 더하면 만들어진 컨텍스트는 두 가지 키를 모두 가진다.

```kotlin


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

```

CoroutineContext에 같은 키를 가진 또 다른 원소가 더해지면 맵처럼 새로운 원소가 기존 원소를 대체한다.

```kotlin

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
```

## 비어 있는 코루틴 컨텍스트

빈 컨텍스트는 원소가 없으므로, 다른 컨텍스트에 더해도 아무런 변화가 없다.

## 원소 제거

```kotlin
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
```

## 텍스트 폴딩

컨텍스트의 각 원소를 조작해야 하는 경우 다른 컬렉션의 fold와 유사한 fold메서드를 사용할 수 있다.

## 코루틴 컨텍스트와 빌더

CoroutineContext는 코루틴의 데이터를 저장하고 전달하는 방법이다.  
부모-자식 관계의 영향 중 하나로 부모는 기본적으로 컨텍스트를 자식에게 전달한다.  
자식은 부모로부터 컨텍스트를 상속받는다고 할 수 있다.

```kotlin
fun CoroutineScope.log(msg: String) {
    val name = coroutineContext[CoroutineName]?.name
    println("[$name] $msg")
}

fun main() = runBlocking(CoroutineName("main")) {
    log("start")                        // [main] start

    val v1 = async {
        delay(500)
        log("Running async")            // [main] Running async
        42
    }

    launch {
        delay(1000)
        log("Running launch")           // [main] Running launch
    }

    log("The answer is ${v1.await()}")  // [main] The answer is 42
}

/*
[main] start
[main] Running async
[main] The answer is 42
[main] Running launch
 */
```

코루틴 컨텍스트 계산 공식

defaultContext + parentContext + childContext

* 새로운 원소가 같은 키를 가진 이전 원소를 대체함. 자식의 컨텍스트는 부모로부터 상속 받은 컨텍스트 중 같은 키를 가진 원소를 대체함.
* Job은 변경이 가능하며, 코루틴의 자식과 부모가 소통하기 위해 사용되는 특별한 컨텍스트이다.

## 중단 함수에서 컨텍스트에 접근하기

CoroutineScope는 컨텍스트를 접근할 때 사용하는 coroutineContext 프로퍼티를 가지고 있다.

```kotlin
suspend fun printName() {
    println(coroutineContext[CoroutineName]?.name)
}

suspend fun main() = withContext(CoroutineName("Outer")) {
    printName()     // Outer

    launch(CoroutineName("Inner")) {
        printName()  // Inner
    }

    delay(10)
    printName()       // Outer
}
```

## 컨텍스트를 개별적으로 생성하기

코루틴 컨텍스트를 커스텀하게 만드는 경우는 흔치 않지만 방법은 간단하다.  
가장 쉬운 방법은 `CoroutineContext.Element` 인터페이스를 구현하는 클래스를 만드는 것이다.

```kotlin
class MyCustomContext : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key

    companion object Key : CoroutineContext.Key<MyCustomContext>
}
```

이렇게 만들어진 컨텍스트는 부모에서 자식으로 전달되고 자식은 같은 키를 가진 또 다른 컨텍스트로 이를 대채할 수 있다는 점에서 CoroutineName과 아주 비슷하다.

```kotlin

class CounterContext(
    private val name: String
) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key
    private var nextNumber = 0

    fun printNext() {
        println("$name: $nextNumber")
        nextNumber++
    }

    companion object Key : CoroutineContext.Key<CounterContext>
}

suspend fun printNext() {
    coroutineContext[CounterContext]?.printNext()
}

suspend fun main(): Unit = withContext(CounterContext("Outer")) {
    printNext()             // Outer : 0
    launch {
        printNext()         // Outer : 1
        launch {
            printNext()     // Outer : 2
        }
        launch(CounterContext("Inner")) {
            printNext()     // Inner : 0
            printNext()     // Inner : 1
            launch {
                printNext() // Inner : 2
            }
        }
    }
    printNext()             // Outer : 3
}
```

CoroutineContext는 맵이나 집합과 같은 컬렉션이랑 개념적으로 비슷하다.  
CoroutineContext는 Element 인터페이스의 인덱싱된 집합이며, Element 또한 CoroutineContext 이다.
CoroutineContext 안의 모든 원소는 식별할 때 사용되는 유일한 Key를 가지고 있다.

## 잡(Job)과 자식 코루틴 기다리기

* 자식은 부모로부터 컨텍스트를 상속받는다.
* 부모는 모든 자식이 작업을 마칠 때까지 기다린다.
* 부모 코루틴이 취소되면 자식 코루틴도 취소된다.
* 자식 코루틴에서 에러가 발생하면, 부모 코루틴 또한 에러로 소멸한다.

## Job이란 무엇인가?

* 잡(job)은 수명을 가지고 있으며 취소가 가능하다.
* 잡(job)의 수명은 상태로 나타낸다.

Active 상태에서는 잡이 실행되고 코루틴은 잡을 수행한다.  
(잡이 코루틴 빌더에 의해 생성되었을 때 코루틴의 본체가 실행되는 상태.)
대부분은 Active로 시작되고, 지연시작되는 코루틴만 New 상태에서 시작한다.  
실행이 완료되면 상태는 Completing으로 바뀌고 자식들을 기다린다.  
자식들의 실행도 모두 끝났다면 잡은 마지막 상태인 Completed로 바뀐다.  
만약, 잡 실행 도중에 취소되거나 실패하게 되면 Cancelling 상태로 변한다.  
후처리 작업이 완료되면 Cancelled 상태가 된다.

## 코루틴 빌더는 부모의 잡을 기초로 자신들의 잡을 생성한다.

코틀린 코루틴 라이브러리의 모든 코루틴 빌더는 자신만의 잡을 생성한다.  
launch의 명시적 반환 타입이 Job이라는 사실을 통해 확인할 수 있다.

```kotlin
fun main() = runBlocking {
    val job: Job = launch {
        delay(1000)
        println("Test")
    }
}

```

## 자식들 기다리기

잡의 첫 번째 중요한 이점은 코루틴이 완료될 때까지 기다리는 데 사용될 수 있다는 점이다.

```kotlin

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
```

## 잡 팩토리 함수

Job은 Job() 팩토리 함수를 사용하면 코루틴 없이도 Job을 만들 수 있다.  
팩토리 함수로 생성하는 잡은 어떤 코루틴과도 연관되지 않으며, 컨텍스트로도 사용될 수 있다.  
`Job()`은 생성자처럼 보이는 간단한 함수로, 가짜 생성자이다.

팩토리 함수가 반환하는 실제 타입은 Job이 아니라 하위 인터페이스인 CompletableJob이다.

# 취소

코틀린 코루틴에서 아주 중요한 기능 중 하나는 취소(Cancellation) 이다.  
취소는 아주 중요한 기능이기 때문에 중단 함수를 사용하는 몇몇 클래스와 라이브러리는 취소를 반드시 지원한다.

(1) 취소 방식이 점점 개선되는 건 금의 무게가 증가하는 것만큼 가치가 있다.  
(2) 단순히 스레드를 죽이면 연결을 닫고 자원을 해제하는 기회가 없기 때문에 최악의 취소 방식이라 볼 수 있다.

## 기본적인 취소

Job 인터페이스는 취소를 하게 하는 cancel 메서드를 가지고 있다.  
Cancel 메서드를 호출하면 다음과 같은 효과를 볼 수 있다.

* 호출한 코루틴은 첫 번째 중단점(아래 예쩨에서는 delay)에서 잡을 끝낸다.
* 잡이 자식을 가지고 있다면, 그들 또한 취소된다. 하지만 부모는 영향을 받지 않는다.
* 잡이 취소되면, 취소된 잡은 새로운 코루틴의 부모로 사용될 수 없다.

```kotlin

suspend fun main() = coroutineScope {
    val job = launch {
        repeat(1000) { i ->
            delay(200)
            println("Printing $i")
        }
    }

    delay(1100)
    job.cancel()
    job.join()
    println("Cancelled successfully")
}
/**
Printing 0
Printing 1
Printing 2
Printing 3
Printing 4
Cancelled successfully
 */

```

cancel이 호출된 뒤 다음 작업을 진행하기 전에 취소 과정이 완료되는 걸 기다리기 위해 join을 사용하는 것이 일반적이다.  
join을 호출하지 않으면 경쟁 상태(race condition)이 될 수 있다.

`cancelAndjoin()` 이라는 확장함수가 존재한다.

## 취소는 어떻게 작동하는가?

잡이 취소되면 'Cancelling' 상태로 변경된다.  
상태가 바뀐 뒤 첫 번째 중단점에서 CancellationException 예외를 던진다.  
try-catch 구문을 사용하여 예외를 잡을 수 있지만 다시 던지는 것이 좋다.

```kotlin
suspend fun main() = coroutineScope {
    val job = Job()
    launch(job) {
        try {
            delay(2000)
            println("Job is done")
        } finally {
            println("Finally")
            launch {
                println("Will not be printed") // 무시된다
            }
            delay(1000) // 예외 발생
            println("Will not be printed")
        }
    }
    delay(1000)
    job.cancelAndJoin()
    println("Cancel done")
}
/*
Finally
Cancel done
 */
```

withContext 내부에서는 취소될 수 없는 Job인 NonCancellable 객체를 사용합니다.  
롤백을 해야되는 경우 사용될 수 있다. (컨텍스트를 바꾸는 것을 포함해서 with context 라고 하는 것 같은데?)

```kotlin
suspend fun main() = coroutineScope {
    val job = Job()
    launch(job) {
        try {
            delay(2000)
            println("Job is done")
        } finally {
            println("Finally")
            withContext(NonCancellable) {
                delay(1000)
                println("clean up")
            }
        }
    }
    delay(1000)
    job.cancelAndJoin()
    println("Cancel done")
}
/*
Finally
clean up
Cancel done
 */
```

## invokeOnCompletion

자원을 해제하는 데 자주 사용되는 또 다른 방법은 Job의 invokeOnCompletion 메서드를 호출하는 것이다.  
invokeOnCompletion 메서드는 잡이 'Completed'나 'Cancelled'와 같은 마지막 상태에 도달했을 때 호출될 핸들러를 지정하는 역할을 한다.    
invokeOnCompletion 은 취소하는 중에 동기적으로 호출되며, 어떤 스레드에서 실행할지 결정할 수 없다.

## 중단될 수 없는 걸 중단하기

취소는 중단점에서 일어나기 때문에 중단점이 없으면 취소를 할 수 없다.  
중단 가능하지 않으면서 CPU 집약적이거나 시간 집약적인 연산들이 중단 함수에 있다면, 각 연산들 사이에 yield를 사용하는 것이 좋다.

코루틴 빌더 내부에 this(리시버)는 빌더 스코프를 참조할 수 있다. CoroutineScope는 coroutineContext 프로퍼티를 사용해 참조할 수 있는 컨텍스트를 가지고 있고,
코루틴 잡에 접근해 현재 상태가 무엇인지 확인할 수 있다.

```kotlin
 val CoroutineScope.isActive: Boolean
    get() = coroutineContext[Job]?.isActive ?: true
```

yield 함수는 전형적인 최상위 중단 함수이다. ㅅ코프가 필요하지 않기 때문에 일반적인 중단 함수에서도 사용될 수 있다.  
중단하고 재개하는 일ㅇㄹ 하기 때문에 스레드 풀을 가진 디스패처를 사용하면 스레드가 바뀌는 문제가 생길 수 있다.  
yield는 CPU 사용량이 크거나 스레드를 블로킹하는 중단 함수에서 자주 사용됩니다.

## suspendCancellableCoroutine

`suspendCancellableCoroutine` 는 라이브러리의 실행을 취소하거나 자원을 해제할 때 사용된다.

```kotlin
suspend fun someTask() = suspendCalcellableCoroutine { count ->
    cont.invokeOnCancellation {
        // 정리 작업을 수행한다.
    }
    // 나머지 구현 부분
}
```

# 예외 처리

코루틴도 잡히지 않은 예외가 발생하면 종료된다.  
코루틴 빌더는 부모도 종료시키며, 취소된 부모는 자식들 모두 취소시킨다.

```kotlin
fun main(): Unit = runBlocking {
    launch {
        launch {
            delay(1000)
            throw Error("Some error")
        }

        launch {
            delay(2000)
            println("Will not be printed")
        }

        launch {
            delay(500) // 예외 발생보다 빠르다.
            println("Will be printed")
        }

    }
    launch {
        delay(2000)
        println("Will not be printed")
    }
}
/*
Will be printed
Exception in thread "main" java.lang.Error: Some error
	at chapter2.ExceptionExamKt$main$1$1$1.invokeSuspend(ExceptionExam.kt:14)
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
	at chapter2.ExceptionExamKt.main(ExceptionExam.kt:10)
	at chapter2.ExceptionExamKt.main(ExceptionExam.kt)
 */
```

부모가 취소되면 자식도 취소되기 때문에 쌍방으로 저파된다.

## 코루틴 종료 멈추기

코루틴 종료전 예외를 잡는 것은 도움이 되지만, 조금이라도 늦으면 이미 도움이 되지 않는다.  
코루틴 간의 상호작용은 잡을 통해 일어나기 때문에, 코루틴 빌더 내부에서 새로운 코루틴 빌더를 try-catch문을 통해 래핑하는건 전혀 도움이 되지 않는다.

```kotlin
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
```

## SupervisorJob

코루틴 종료를 멈추는 가장 중요한 방법은 SupervisorJob을 사용하는 것이다.  
SupervisorJob을 사용하면 자식에서 발생한 모든 예외를 무시할 수 있다.

## SupervisorScope

예외 전파를 막는 또 다른 방법은 코루틴 빌더를 supervisorScope로 래핑하는 것이다.  
supervisorScope는 단지 중단 함수일 뿐이며, 중단 함수 본체를 래핑하는 데 사용된다.  
SupervisorScope를 사용하는 일반적인 방법은 서로 무관한 다수 작업을 스코프 내에서 실행하는 것이다.

```kotlin
suspend fun notifyAnalytics(actions: List<UserAction>) =
    supervisorScope {
        actions.forEach { action ->
            launch {
                notifyAnalytics(action)
            }
        }
    }
```

예외 전파를 멈추는 다른 방법으로는 coroutineScope를 사용하는 것이다.  
이 함수는 코루틴 빌더와 달리 부모에 영향을 미치는 대신 try-catch를 이용해 잡을 수 있는 예외를 던진다.

## await

예외가 발생했을 때 async 코루틴 빌더는 launch처럼 부모 코루틴을 종료하고 부모와 관련있는 다른 코루틴 빌더도 종료시킨다.

## CancellationException은 부모까지 전파되지 않는다.

CancellationException의 서브클래스면 부모로 전파되지 않는다.  
현재 코루틴을 취소시킨다.

## 코루틴 예외 핸들러

```kotlin
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

```

# 코루틴 스코프 함수

## coroutineScope

coroutineScope는 스코프를 시작하는 중단 함수이며, 인자로 들어온 함수가 생성한 값을 반환 합니다.

```kotlin
import kotlin.coroutines.CoroutineContext

suspend fun <R> coroutineScope(
    block: suspend CoroutineScope.() -> R
): R
```

async나 launch와 다르게 coroutineScope의 본체는 리시버 없이 곧바로 호출된다.  
coroutineScope 함수는 새로운 코루틴을 생성하지만 새로운 코루틴이 끝날 때까지 coroutineScope를 호출한 코루틴을 중단하기 때문에 호출한 코루틴이 작업을 동시에 시작하지 않는다.

```kotlin
import kotlin.coroutines.coroutineContext

fun main() = runBlocking {
    val a = coroutineScope {
        delay(1000)
        10
    }
    println("a is calculated")
    val b = coroutineScope {
        delay(1000)
        20
    }

    println(a)
    println(b)
}
```

생성된 스코프는 바깥의 스코프에서 coroutineContext를 상송 받지만 컨텍스트의 job을 오버라이딩한다.  
따라서 생성된 스코프는 부모가 해야 할 책임을 이어 받는다.

* 부모로부터 컨텍스트를 상속받음
* 자신의 작업을 끝내기 전까지 모든 자식을 기다림
* 부모가 취소되면 자식들 모두 취소됨

```kotlin
import kotlin.coroutines.coroutineContext

suspend fun longTask() = coroutineScope {
    launch {
        delay(1000)
        val name = coroutineContext[CoroutineName]?.name
        println("[$name] Finished task 1")
    }

    launch {
        delay(2000)
        val name = coroutineContext[CoroutineName]?.name
        println("[$name] Finished task 2")
    }
}

fun main() = runBlockig(CoroutineName("Parent")) {
    println("Before")
    longTask()
    println("After")
}

// 1.5초 뒤 cancel되서 longTask>첫번째launch만 실행
```

coroutineScope 함수는 기존의 중단 컨텍스트에서 벗어난 새로운 스코프를 만든다.  
부모로부터 스코프 상속받고 구조화된 동시성을 지원한다.

## 코루틴 스코프 함수

* supervisorScope는 coroutineScope와 비슷하지만, Job 대신 SupervisorJob을 사용한다.
* withContext는 코루틴 컨텍스트를 바꿀 수 있는 coroutineScope이다.
* withTimeout은 타임아웃이 있는 coroutineScope 이다.

## withContext

withContext 함수는 coroutineScope와 비슷하지만 스코프의 컨텍스트를 변경할 수 있다는 특징이 있다.  
withContext의 인자로 컨텍스트를 제공하면(코루틴 빌더와 같은 방식으로) 부모 스콮의 컨텍스트를 대채한다.

```kotlin
fun CoroutineScope.log(text: String) {
    val name = this.coroutineContext[CoroutineName]?.name
    println("[$name] $text")
}
```

withContext 함수는 기존 스코프와 다른 컨텍스트가 다른 코루틴 스코프를 설정하기 위해 사용된다.

## supervisorScope

supervisorScope 함수는 호출한 스코프로부터 상속받은 CoroutineScope를 만들고 지정된 중단 함수를 호출한다는 점에서 coroutineScope와 비슷하다.  
둘의 차이는 컨텍스트의 Job을 SupervisorJob으로 오버라이딩 하는 것이기 때문에 자식 코루틴이 예외를 던지더라도 취소되지 않는다.

(Overriding 하는 것)

코루틴 컨텍스트를 overriding 하기 떄문에 자식에서 exception이 발생되어도, 취소가 되지 않음.  
취소는 부모 컨텍스트를 물려받은 자식 컨텍스트에서 취소가 발생되면, 자식과 부모 둘다 취소시켜야 하는 것  
[구조화된 동시성 참고](#구조화된-동시성)

## withTimeout

withTimeout 스코프는 인자로들어온 람다식을 실행할 때 시간 제한이 있는 것이 특징이다.  
실행하는데 시간이 너무 오래 걸리면 람다식은 취소되고 (CancellationException의 서브타입인 TimeoutCancellationException을 던진다.

//TODO 코드 경로 추가

withTimeoutOrNull은 예외를 던지지 않는다. 타임아웃을 초과하면 람다식이 취소되고 null이 반환된다.

## 코루틴 스코프 함수 연결하기

서로 다른 코루틴 스코프 함수의 두 가지 기능이 모두 필요하다면 코루틴 스코프 함수에서 다른 기능을 가지는 코루틴 스코프 함수를 호출해야 한다.

```kotlin
suspend fun calculateAnswerOrNull(): User? =
    withContext(Dispatchers.Default) {
        withTimeoutOrNull(1000) {
            calculateAnswer()
        }
    }
```

# 디스패처

코틀린 코루틴 라이브러리가 제공하는 중요한 기능은 코루틴이 실행되어야(시작하거나 재개하는 등) 할 스레드(또는 스레드 풀)를 결정할 수 있다는 것이다.

## 기본 디스패처

디스패처를 설정하지 않으면 기본적으로 설정되는 디스패처는 CPU 집약적인 연산을 수행하도록 설계된 Dispatchers.Default이다.  
이 디스패처는 코드가 실행되는 컴퓨터의 CPU 개수와 동일한 수(최소 두 개 이상)의 스레드 풀을 가지고 있다.  
스레드를 효율적으로 사용하고 있다고 가정하면 이론적으로 최적의 스레드 수라고 할 수 있다.

```kotlin
suspend fun main() = coroutineScope {
    repeat(1000) {
        launch {
            List(1000) { Random.nextLong() }.maxOrNull()
            val threadName = Thread.currentThread().name
            println("Running on thread : $threadName")
        }
    }
}
```

## 기본 디스패처 제한하기

비용이 많이 드는 작업이 Dispatchers.Default의 스레드를 다 써버려서 같은 디스패처를 사용하는 다른 코루틴이 실행될 기회를 제한하고 있다고 의심하는 상황을 떠올려 보자.  
이런 상황을 마주쳤을 때 Dispatchers.Default의 limitedParallelism을 사용하면 디스패처가 같은 스레드 풀을 사용하지만 같은 시간에 특정 수 이상의 스레드를 사용하지 못하도록 제한할 수
있다.

```kotlin
private val dispatcher = Dispatchers.Default.limitedParallelism(5)
```

디스패처의 스레드 수를 제한하는 방법은 Dispatchers.Default에만 사용되는 것은 아니다.

## 메인 디스패처

메인 스레드에서 코루틴을 실행할 때 사용되는 디스패처는 Dispatchers.Main 이다.  
복잡한 연산을 하지 않는다면 Dispatchers.Main만으로 충분하다.  
CPU에 집약적인 작업을 수행한다면 Dispatchers.Default로 실행해야 한다.
대부분의 애플리케이션에는 두개의 디스패처만 있어도 충분하다.

## IO 디스패처

Dispatchers.IO는 파일을 읽고 쓰는 경우, 안드로이드의 셰어드 프레퍼런스(shared preference)를 사용하는 경우, 블로킹 함수를 호출하는 경우처럼 I/O 연산으로 스레드를 블로킹 할 때 사용하기
위해 설계되었다.

```kotlin
suspend fun main() {
    val time = measureTimeMillis {
        coroutineScope {
            repeat(50) {
                launch(Dispatchers.IO) {
                    Thread.sleep(1000)
                }
            }
        }
    }
    println(time)
}

// 1035
```

50개가 넘는 스레드를 사용할 수 있도록 만들어져 있기 때문에 1초 밖에 걸리지 않는다.

Dispatchers.ID를 사용하는 가장 흔한 경우는 라이브러리에서 블로킹 함수를 호출해야 하는 경우이다. 이런 경우 withContext(Dispatchers.IO)로 패킹해 중단 함수로 만드는 것이 가장
좋다.  
withContext(Dispatchers.IO)로 래핑한 함수가 너무 많은 스레드를 블로킹하면 문제가 될 수 있다.  
Dispatchers.ID로 생성된 스레드 수 보다 많은 수의 스레드를 사용하면 자기 차례가 돌아올 떄 까지 스레드 전부를 기다리게 할 수 있다.  
이런 경우 limitedParallelism을 사용할 수 있다.

## 커스텀 스레드 풀을 사용하는 IO 디스패처

Dispatchers.IO에는 limitedParallelism 함수를 위해 정이된 특별한 작동 방식이 있다.  
이렇게 만들어진 풀은 우리가 원하는 만큼의 많은 수의 스레드를 설정할 수 있게 한다.  
limiedParallism을 가장 잘 활용하는 방법은 스레드를 블로킹하는 경우가 잦은 클래스에서 자기만의 한도를 가진 커스텀 디스패처를 정의하는 것이다.

```kotlin
suspend fun main(): Unit = coroutineScope {
    launch {
        printCoroutinesTime(Dispatchers.IO)
        // Dispatchers.IO took: 2029

    }

    launch {
        val dispatcher = Dispatchers.IO
            .limitedParallelism(100)
        printCoroutinesTime(dispatcher)
        // LimitedDispatcher@4c8dbae2 took: 1034
    }

}

suspend fun printCoroutinesTime(
    dispatcher: CoroutineDispatcher
) {
    val test = measureTimeMillis {
        coroutineScope {
            repeat(100) {
                launch(dispatcher) {
                    Thread.sleep(1000)
                }
            }
        }
    }
    println("$dispatcher took: $test")
}
```

limitedParallism을 가장 잘 활용하는 방법은 스레드를 블로킹하는 경우가 잦은 클래스에서 자기만의 한도를 가진 커스텀 디스패처를 정의하는 것이다.  
한도 설정은 정해진 답이 없다. 너무 많으면 자원을 비효율적으로 사용하게 되고, 너무 적어도 가능한 스레드를 기다리게 되므로 성능이 좋지 않다.

## 정해진 수의 스레드 풀을 가진 디스패처

몇몇 개발자들은 자신들이 사용하는 스레드 풀을 직접 관리하기를 원하며, 자바는 이를 지원하기 위한 강력한 API를 제공한다.  
예를 들면 Executors 클래스를 스레드의 수가 정해져 있는 스레드 풀이나 캐싱된 스레드 풀을 만들 수 있다.
코틀린에서는 `asCoroutineDispatcher` 함수를 이용해 디스패처로 변형하는 것이 가능하다.

```kotlin
import java.util.concurrent.Executors

val NUMBER_OF_THREAD = 20
val dispatcher = Executors.newFixedThreadPool(NUMBER_OF_THREAD)
    .asCoroutineDispatcher()
```

위와 같이 만들어진 디스패처의 가장 큰 문제점은 close 함수로 닫혀야 된다는 것이다. 이를 누락하면 스레드 누수가 발생할 수 있다.

## 싱글스레드로 제한된 디스패처

다수 스레드를 사용하는 모든 디스패처에서는 공유 상태 문제로 인한 문제를 생각해야 한다.
공유상태의 문제를 해결하기 위해서는 싱글스레드 디스패처를 만드는 것이 대표적이라고 하지만, 이 스레드가 블로킹되면 작업이 순차적으로 처리되는 문제가 발생도기 때문에 큰 단점이다.

## 프로젝트 룸의 가상 스레드 사용하기

JVM 플랫폼은 프로젝트 룸(Project Loom) 이라는 새로운 기술을 발표했다. 프로젝트 룸의 가장 혁신적인 특징은 일반적인 스레드보다 훨씬 가벼운 가상 스레드를 도입했다는 점이다.  
일반적인 스레드를 블로킹하는 것보다 가상 스레드를 블로킹 하는 것이 비용이 훨씬 적게된다.

코틀린 코루틴을 알고 있는 개발자들은 프로젝트 룸을 사용할 필요가 별로 없다.  
코틀린 코루틴은 취소를 쉽게 할 수 있으며, 테스트에서 가상 시간을 쓰는 등의 훨씬 더 놀라운 기능을 갖추고 있다.  
프로젝트 룸이 정말로 유용한 경우는 스레드를 블로킹할 수 밖에 없는 Dispatchers.IO 대신 가상 스레드를 사용할 때이다.

```kotlin

suspend fun main() = measureTimeMillis {
    coroutineScope {
        repeat(100000) {
            launch(Dispatchers.Loom) {
                Thread.sleep(1000)
            }
        }
    }
}.let(::println)
// 1860

object LoomDispatcher : ExecutorCoroutineDispatcher() {
    override val executor = Executor { command ->
        Thread.startVirtualThread(command)
    }

    override fun close() {
        error("Cannot be invoked on Dispatchers.LOOM")
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        executor.execute(block)
    }
}

val Dispatchers.Loom: CoroutineDispatcher
    get() = LoomDispatcher

```

```kotlin

suspend fun main() = measureTimeMillis {
    val dispatcher = Dispatchers.IO
        .limitedParallelism(100000)

    coroutineScope {
        repeat(100000) {
            launch(dispatcher) {
                Thread.sleep(1000)
            }
        }
    }
}.let(::println)
// 24277

```

코틀린 코루틴 팀이 프로젝트 룸이 안정화되면 가상 스레드를 기본으로 사용할 수 있다고 했기 때문에 나웆ㅇ에는 룸 디스패처가 필요하지 않을 수 있다.

## 제한 받지 않는 디스패처

Dispatchers.Unconfined는 스레드를 바꾸지 않는다는 점에서 이전 디스패처들과 다르다.  
제한 받지 않는 디스패처가 시작되면 시작한 스레드에서 실행이 된다.  
재개되었을 때는 재개한 스레드에서 실행된다.

## 메인 디스패처로 즉시 옮기기

코루틴을 배경하는 것도 비용이 든다. withContext가 호출되면 코루틴은 중단되고 큐에서 기다리다가 재개된다.

`Dispatchers.Main.immediate` 를 스레드 배정 없이 즉시 실행된다.  
함수가 메인 디스패처에서 호출되었다면, withContext의 인자로 `Dispatchers.Main.immediate` 를 쓰는것이 좋다.  
메인 디스패처 외의 다른 디스패처에서는 즉시 배정하는 옵션을 현재 지원하지 않는다.

## 컨티뉴에이션 인터셉터

디스패칭은 코틀린 언어에서 지원하는 컨티뉴에이션 인터셉터를 기반으로 작동하고 있다.  
ContinuationInterceptor라는 코루틴 컨텍스트는 코루틴이 중단되었을 때 interceptorContinuation 메서드로 컨티뉴에이션 객체를 수정하고 포장한다.  
releaseInterceptedContinuation 메서드는 컨티뉴에이션이 종료되었을 때 호출 된다.

```kotlin

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

interface ContinuationInterceptor :
    CoroutineContext.Element {
    companion object Key :
        CoroutineContext.Key<ContinuationInterceptor>

    fun <T> interceptContinuation(
        continuation: Continuation<T>
    ): Continuation<T>

    fun releaseInterceptedContinuation(
        continuation: Continuation<*>
    ) {

    }
    // ...
}
```

DispatchedContinuation은 디스패처가 작동하는 핵심 요소다.

## 작업의 종류에 따른 각 디스패처의 성능 비교

* 단지 중단될 경우에는 사용하고 있는 스레드 수가 얼마나 많은지 문제가 되지 않는다.
* 블로킹할 경우에는 스레드 수가 많을 수록 모든 코루틴이 종료되는 시간이 빨라진다.
* CPU 집약적인 연산에서는 Dispatchers.Default가 가장 좋은 선택지다.
* 메모리 집약적인 연산을 처리하고 있다면 더 많은 스레드를 사용하는 것이 좀더 낫다.

# 코루틴 스코프 만들기

## CoroutineScope 팩토리 함수

CoroutineScope는 coroutineContext를 유일한 프로퍼티로 가지고 있는 인터페이스이다.

```kotlin
import kotlin.coroutines.CoroutineContext

interface CoroutineScope {
    val coroutineContext: CoroutineContext
}
```

코루틴 스코프 인스턴스를 프로퍼티로 가지고 있다가 코루틴 빌더를 호출 할 때 사용하는 방법이 선호 된다.

```kotlin
class SomeClass {
    val scope: CoroutineScope = ...
    fun onStart() {
        scope.launch {
            // ...
        }
    }
}
```

코루틴 스코프 객체를 만드는 가장 쉬운 방법은 CoroutineScope 팩토리 함수를 사용하는 것이다.

context를 공유하거나 결합할 수 있으니, 각각 작업마다 결합하여 작성하고, 이렇게 컨텍스트를 공유하게 되면, 부모 자식 관계처럼 구성되어, 자식이 취소되면 부모도 취소되도록 구성하는 방식으로 동작하는 것인가?

GPT
코루틴 컨텍스트를 공유하거나 결합하는 것은 Kotlin 코루틴에서 중요한 부분입니다. 하지만, 부모와 자식 코루틴 사이의 취소 정책에 대한 설명에는 약간의 정정이 필요합니다. 실제로 동작하는 방식은 다음과 같습니다:

코루틴 컨텍스트의 공유 및 결합
코루틴 컨텍스트는 여러 요소를 결합하여 새로운 컨텍스트를 생성할 수 있습니다. 예를 들어, Dispatchers.IO와 사용자 정의 CoroutineName을 결합할 수 있습니다. 이렇게 결합된 컨텍스트는 해당
코루틴이 실행될 스레드와 함께 추가적인 정보(예: 코루틴 이름)를 제공합니다.

```kotlin
val context = Dispatchers.IO + CoroutineName("myCoroutine")

```

부모-자식 코루틴 관계
부모-자식 관계에서 중요한 것은, 자식 코루틴이 취소되더라도 기본적으로 부모 코루틴에는 영향을 미치지 않는다는 점입니다. 반대로, 부모 코루틴이 취소되면 그에 속한 모든 자식 코루틴도 취소됩니다.

부모 취소 시 자식 취소: 부모 코루틴이 취소되면, 그 자식 코루틴들은 모두 취소됩니다. 이는 코루틴 계층을 통해 취소 신호가 전파되기 때문입니다.
자식 취소 시 부모 영향 없음: 하나의 자식 코루틴이 취소되어도 다른 자식 코루틴이나 부모 코루틴에는 기본적으로 영향을 미치지 않습니다. 다만, 특정 로직을 통해 자식 코루틴의 실패가 부모 코루틴에 전파되도록 구성할
수는 있습니다.

```kotlin
val parentJob = CoroutineScope(Dispatchers.Default).launch {
    val childJob = launch {
        // 자식 코루틴의 작업
    }
    // 부모 코루틴의 작업
}

// 부모 코루틴 취소
parentJob.cancel()

```

이 예제에서 parentJob.cancel()을 호출하면 childJob도 취소됩니다. 하지만 childJob.cancel()을 호출해도 parentJob에는 영향을 미치지 않습니다.

코루틴의 부모-자식 관계는 코루틴 계층을 통한 구조적인 동시성 관리에 유용합니다. 이를 통해 개발자는 복잡한 비동기 프로그램을 더 쉽게 관리하고, 예외 상황에 대응할 수 있습니다.

## 백엔드에서 코루틴 만들기

많은 백엔드 프레임워크에서 중단 함수를 기본적으로 지원하고 있다.  
spring boot는 컨트롤러 함수가 suspend로 선언되는 걸 허용합니다.

* 스레드 풀(또는 Dispatchers.Default)을 가진 커스텀 디스패처
* 각각의 코루틴을 독립적으로 만들어 주는 SupervisorJob
* 적절한 에러 코드에 응답하고, 데드 레터를 보내거나, 발생한 문제에 대해 로그를 남기는 CoroutineExceptionHandler

```kotlin
@Configuration
class CoroutineScopeConfiguration {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Bean
    fun coroutineDispatcher(): CoroutineDispatcher =
        Dispatchers.IO.limitedParallelism(5)

    @Bean
    fun coroutineExceptionHandler() = CoroutineExceptionHandler { _, throwable ->

    }

    @Bean
    fun coroutineScope(
        coroutineDispatcher: CoroutineDispatcher,
        coroutineExceptionHandler: CoroutineExceptionHandler
    ) = CoroutineScope(
        SupervisorJob() +
                coroutineDispatcher + coroutineExceptionHandler
    )
}

```

## 추가적인 호출을 위한 스코프 만들기

추가적인 연산을 시작하기 위한 스코프를 종종만들곤 하는데, 이런 스코프는 함수나 생성자의 인자를 통해 주로 주입된다.  
스코프를 호출을 중단하기 위한 목적으로만 사용하려는 경우 SupervisorSocpe를 사용하는 것만으로 충분하다.

스코프에서 블로킹을 호출하면 Dispatchers.IO를 사용하고, 안드리오드의 메인 뷰를 다뤄야 한다면 Dispatchers.Main을 사용한다.

# 공유상태로 인한 문제

공유자원 문제가 발생할 수 있는 변수 선언을 하게 되면, 예상했던 동작과 다른 동작을 할 수 있다는 내용

## 동기화 블로킹

공유 자원 문제를 해결하기 위해 자바에서는 synchronized 블록이나 동기화된 컬렉션을 사용해 해결할 수 있다.  
이 방법이 작동하지만 몇 가지 문제사항이 있다.

가장 큰 문제점은 synchronized 블록 내에서는 중단 함수를 사용할 수 없다.  
두 번째는 synchronized 블록에서 코루틴이 자기 차례를 기다릴 때 스레드를 블로킹 한다는 것이다.

## 원자성

자바는 다양한 원자값을 가지고 있다. 원자값을 활용한 연산은 빠르게 '스레드 안전'을 보장한다.
이런 연산을 원자성 연산이라 합니다.

## 싱글 스레드로 제한된 디스패처

공유상태와 관련된 문제를 해결하는 쉬운 방법은 싱글스레드 디스패처를 사용하는 것이다.

```kotlin
val dispatcher = Dispatchars.IO
    .limitedParallelism(1)

var counter = 0

fun main() = runBlocking {
    massvieRun {
        withContext(dispatcher) {
            counter++
        }
    }
    println(counter) // 1000000
}
```

두 가지 방법으로 디스패처를 사용할 수 있다.  
첫 번째 방법은 코스 그레인드 스레드 한정(coarse-grained thread confinement)로 알려져 있다.
이 방법은 디스패처를 싱글스레드로 제한한 withContext로 전체 함수를 래핑하는 방법이다.

### 코스 그레인드 스레드 한정이란?

코스 그레인드 스레드 한정(Coarse-Grained Thread Confinement)은 멀티스레드 프로그래밍에서 데이터 또는 작업을 특정 스레드에 한정시켜 안전성을 보장하는 기법입니다. 이 방법은 주로 데이터의
일관성과 동기화 문제를 방지하기 위해 사용됩니다. 코스 그레인드(거친 단위) 한정이라는 용어는 상대적으로 큰 작업 단위 또는 데이터 구조가 단일 스레드에 의해서만 접근되거나 수정됨을 의미합니다.

#### 코스 그레인드 스레드 한정의 장점:

* 단순성: 복잡한 동기화 없이도 스레드 안전성을 보장할 수 있습니다.
* 성능: 락(lock)을 사용하지 않거나 최소화하여 오버헤드를 줄일 수 있습니다.
* 예측 가능성: 데이터가 어느 스레드에 의해 접근될지 명확하기 때문에 디버깅과 유지보수가 쉬워집니다.
* 코스 그레인드 스레드 한정의 단점:
    * 유연성 제한: 모든 관련 작업을 같은 스레드에서 처리해야 하므로, 병렬 처리의 이점을 활용하기 어려울 수 있습니다.
    * 스레드 활용도: 특정 스레드에 과도한 작업이 집중될 수 있어 시스템의 전체적인 스레드 활용도가 낮아질 수 있습니다.

예제 코드 (Kotlin 코루틴을 사용한 예):  
Kotlin에서는 코루틴을 사용하여 코스 그레인드 스레드 한정을 쉽게 구현할 수 있습니다. 아래 예제에서는 Dispatchers.Main을 사용하여 모든 작업을 메인 스레드에 한정시키는 방법을 보여줍니다. 실제
안드로이드 애플리케이션 개발에서 UI 작업은 메인 스레드에서 수행되어야 합니다.

```kotlin

import kotlinx.coroutines.*

fun main() = runBlocking {
    // 메인 스레드에 한정된 코루틴 스코프 생성
    val mainScope = CoroutineScope(Dispatchers.Main)

    mainScope.launch {
        // 여기에 UI 업데이트 또는 메인 스레드에서 처리해야 할 작업을 넣습니다.
        println("This is running on the main thread.")
    }
}

```

## 뮤텍스

마지막으로 인기 있는 방식은 Mutex를 사용하는 것이다.  
뮤텍스를 단하나의 열쇠가 있는 방(또는 카페테리아의 화장실) 이라고 할 수 있다.

뮤텍스의 가장 중요한 기능은 Lock이다. 첫 번째 코루틴이 lock을 호출하면 열쇠를 가지고 중단 없이 작업을 수행한다.  
또 다른 코루틴이 lock을 호출하면 첫 번째 코루틴이 unlock을 호출할 때까지 중단 된다.

```kotlin
suspend fun main() = coroutineScope {
    repeat(5) {
        launch {
            delayAndPrint()
        }
    }
}

val mutex = Mutex()
suspend fun delayAndPrint() {
    mutex.lock()
    delay(1000)
    println("Done")
    mutex.unlock()
}

```

mutex를 이용하면 lock과 unlock을 사용할 때 두 함수 사이에 예외가 발생되어 deadlock이 걸릴 수 있는 문제가 있다.  
이런 문제를 해결하기 위해 lock으로 시작해 finally 블록에서 unlock을 호출하는 withLock 함수를 사용하여 블록 내에서 어떤 예외가 발생하더라도 자물쇠를 성공적으로 풀 수 있게 할 수 있다.   
실제 사용하는 방법은 synchronized 블록과 비슷하다.

뮤텍스를 사용할 때 위험한 경우는 코루틴이 락을 두 번 통과할 수 없다는 것이다.  
뮤텍스가 가진 두 번째 문제점은 코루틴이 중단되었을 때 뮤텍스를 풀 수 없다는 점이다.

전체 함수를 뮤텍스로 래핑하는 것은 지양해야 한다.   
뮤텍스를 사용하기로 했다면 락을 두 번 걸지 않고 중단 함수를 호출하지 않도록 신경을 써야 한다.

## 세마포어

Mutex는 하나의 접근만 허용하므로, lock, unlock, withLock 함수를 가지고 있다.
Semaphore는 여러개의 접근을 허용하므로, acquire, release, withPermit 함수를 가지고 있다.

```kotlin
suspend fun main() = coroutineScope {
    val semaphore = Semaphore(2)

    repeat(5) {
        launch {
            semaphore.withPermit {
                delay(1000)
                print(it)
            }
            println()
        }
    }
}
```

세마포어는 공유 상태로 인해 생기는 문제를 해결 할 수는 없지만, 동시 요청을 처리하는 수를 제한할 때 사용할 수 있어 `처리율 제한 장치(rate limiter)`를 구현할 때 도움이 됩니다.
