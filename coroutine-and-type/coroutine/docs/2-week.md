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

컨텍스트에서 모든 원소는 식별할 수 있는 유일한 Key를 가지고 있다.
