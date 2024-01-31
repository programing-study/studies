# Kotlin Coroutine을 배워야 하는 이유

이미 여러 비동기적 연산을 수행하는 방법이 있다.

- Rector
- RxJava

코틀린 코루틴은 기존 방식보다 훨씬 많은 것을 제공한다.
1963년 처음 제시되었지만, 실제 현업에서 사용할 수 있도록 구현하기 까지 수십년이 걸렸다.

코틀린 코루틴은 모든 플랫폼(JVM, JS, iOS) 등을 넘나들며 사용할 수 있다.

그리고 기존 코드를 광범위하게 뜯어고칠 필요가 없다.

``` kotlin
   fun onCreate() {
      val news = getNewsFromApi()
      val sortedNews = news
           .sortedByDescending { it.publishedAt }
      view.showNews(sortedNews)
   }
```

위와 같은 코드를 비동기적으로 작성하는 방법에 대해 알아보자

스레드로 전환하게된다면 아래와 같이 작성하면 된다.

``` kotlin
   fun onCreate() {
      thread {      
          val news = getNewsFromApi()
          val sortedNews = news
               .sortedByDescending { it.publishedAt }
          view.showNews(sortedNews)
      }
   }
```

위와 같이 작성했을 경우, 실행을 하게되면 비동기적인 스레드가 실행되어, 메인 스레드에서는 제어가 불가능합니다.
그리고, 이런 코드가 자주 반복되면 스레드를 많이 생성하기 때문에 비용(메모리)이 많이듭니다.

이런 형식으로 front에서 `onCreate()` 펑션을 API형태로 요청하고 응답이 오기전에 다른 화면으로 이동하게 된다던지하는 경우
이미 실행된 thread를 제어할 방법이 없습니다. 책에서는 안드로이드를 기준으로 설명하고 있고, 안드로이드 같은 경우에는 view를 제어하는 thread는 싱글스레드로 동작하며,
화면 이동같은 행위로 인하여 존재하지 않는 뷰를 고치는 상황이 발생되어 예외가 발생한다고 합니다.

이런 문제를 풀기 위해서 다음 방법으로는 callback이 있습니다.

``` kotlin
   fun onCreate() {
     getConfigFromApi { config ->
        getNewsFromApi(config) -> { news ->
            getUserFromApi { user ->
                view.showNews(user, news)
                }
             }
          } 
     }
   }
```

콜백 구조를 이용하여 병렬로 처리할 수 있지만, 이전 작업을 취소하는 방법을 고려해보면 방법이 쉽지 않다.
defer (promise) 라는 형태로 작성되는 callback 구조가 이전에 유행했던 이유가 이런 이유를 해결하려 적용한 것으로 알고 있다.

```javascript
function asyncTask(id, fail = false) {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            if (fail) {
                reject(`Task ${id} failed`);
            } else {
                resolve(`Result of task ${id}`);
            }
        }, 1000);
    });
}

const promise1 = asyncTask(1, Math.random() > 0.5).catch(error => `Error in task 1: ${error}`);
const promise2 = asyncTask(2, Math.random() > 0.5).catch(error => `Error in task 2: ${error}`);
const promise3 = asyncTask(3, Math.random() > 0.5).catch(error => `Error in task 3: ${error}`);

Promise.all([promise1, promise2, promise3])
    .then(results => {
        console.log('All tasks completed:', results);
    })
    .catch(error => {
        console.error('An error occurred:', error);
    });

```

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureExceptionHandlingExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture
                .supplyAsync(() -> {
                    if (Math.random() > 0.5) {
                        throw new RuntimeException("Something went wrong in Future 1!");
                    }
                    return "Result of Future 1";
                })
                .exceptionally(ex -> "Error in Future 1: " + ex.getMessage());

        CompletableFuture<String> future2 = CompletableFuture
                .supplyAsync(() -> {
                    if (Math.random() > 0.5) {
                        throw new RuntimeException("Something went wrong in Future 2!");
                    }
                    return "Result of Future 2";
                })
                .exceptionally(ex -> "Error in Future 2: " + ex.getMessage());

        CompletableFuture<String> future3 = CompletableFuture
                .supplyAsync(() -> {
                    if (Math.random() > 0.5) {
                        throw new RuntimeException("Something went wrong in Future 3!");
                    }
                    return "Result of Future 3";
                })
                .exceptionally(ex -> "Error in Future 3: " + ex.getMessage());

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2, future3);

        // 모든 Future가 완료될 때까지 기다립니다
        combinedFuture.get();

        // 이제 각 Future의 결과를 출력합니다
        System.out.println(future1.get());
        System.out.println(future2.get());
        System.out.println(future3.get());
    }
}

```

이런 구조로 코드를 작성하게 되는 경우 들여쓰기가 많아지고, 읽기 힘들어지는 상황이 발생되는데 이런 것을 callback hell 이라고 말한다.
Node.js 프로젝트에서 쉽게 확인할 수 있다.

## 코틀린 코루틴 사용

코틀린 코루틴이 도입한 핵심 기능은 코루틴을 특정 지점에서 멈추고 이후에 재개할 수 있다는 것이다.  
코루틴을 사용하면 우리가 짠 코드를 메인스레드에서 실행하고, API에서 데이터를 얻어올 때 잠깐 중단시킬 수도 있다.
코루틴을 중단시켰을 때 스레드는 블로킹되지 않으며 뷰를 바꾸거나 다른 코루틴을 실행하는 등의 또 다른 작업이 가능하다.

> 코루틴을 정의하면, 중단했다가 다시 실행할 수 있는 컴포넌트라고 할 수 있다.
> javascript, Rust, Python에서는 async/await, 제너레이터와 같은 개념을 코루틴에서 다루지만 아주 제한적이다.

```kotlin
fun onCreate() {
    viewModelScope.lauch {
        val config = getConfigFromApi()
        val news = getNewsFromApi()
        val user = getUserFromApi()
        view.showNews(user, news)
    }
}
```

위 코드에서는 scope(코틀린의 실행범위)가 지정되어 있다.

코드는 메인 스레드에서 실행되지만, 스레드를 블로킹하지 않는다. 코루틴의 중단은 데이터가 오는 걸 기다릴 때(스레드를 블록킹 하는 대신) 코루틴을 잠시 멈추는 형태로 작동한다.

코루틴이 멈춰 있는 동안, 메인 스레드는 다른 작업을 할 수 있고, 데이터가 준비되면 메인 스레드를 할당받아 이전에 멈춘 지점부터 다시 시작할 수 있다.

async/await로도 작성이 가능하다.

```kotlin
fun onCreate() {
    viewModelScope.lauch {
        val config = async { getConfigFromApi() }
        val news = async { getNewsFromApi() }
        val user = async { getUserFromApi() }
        view.showNews(user.await(), news.await())
    }
}
```

scope를 나누게 되면 작업단위를 작게 만들 수 있다.

## 백엔드에서 코루틴 사용

백엔드에서 코루틴을 사용하는 경우 가장 큰 장점은 간결성이다.
Rx-Java와 달리 코루틴을 도입하면 현재 코드에 큰 변화가 없이 작성이 가능하다.
코루틴으로 바꾸는 대부분의 환경에서는 단지 suspend 제어자(modifier)를 추가하는 것으로 충분하다.

그리고 코루틴을 사용하는 가장 중요한 이유는 스레드를 사용하는 비용이 크기 때문이다. 스레드는 명시적으로 생성해야 하고,
유지되어야하며, 스레드를 위한 메모리를 할당해야 한다.
(스레드 스택의 기본 크기는 대략적으로 1MB 정도 이다. 정확히 1MB는 아니지만 많은 스레드가 만들어질 수록 증가되는 것을 알아두자 )

# 시퀀스 빌더

코틀린에서는 제너레이터 대신 시퀀스를 생성할 때 사용하는 시퀀스 빌더를 제공하고 있다.

코틀린의 시퀀스는 List나 Set과 같은 컬렉션이랑 비슷한 개념이지만, 필요할 때마다 값을 하나씩 계산하는 지연(lazy) 처리를 한다.
시퀀스 특징은 다음과 같다.

- 요구되는 연산을 최소한으로 수행한다.
- 무한정이 될 수 있다.
- 메모리 사용이 효율적이다.

시퀀스 예제를 살펴보자

sequence라는 함수를 이용해 정의해보자.

```kotlin
val seq = sequence {
    yield(1)
    yield(2)
    yield(3)
}

fun main() {
    for (num in seq) {
        print(num)
    } // 123
}
```

이 객체는 시퀀스 람다 표현식 내부에서는 yield 함수를 호출하여 시퀀스의 다음 값을 생성한다.  
여기서 반드시 알아야 하는 것은 각 숫자가 미리 생성되는 대신, 필요할 때마다 생성된다는 것이다.

다음 예제를 보자

```kotlin
val seq2 = sequence {
    println("Generating first")
    yield(1)
    println("Generating second")
    yield(2)
    println("Generating third")
    yield(3)
}

fun main() {
    for (num in seq2) {
        println("The next number is $num")
    }
}

/*
Generating first
The next number is 1
Generating second
The next number is 2
Generating third
The next number is 3
 */
```

위 코드를 이용하여 main function에는 println, seq2에서는 yield 함수에 break point를 걸어두고 debug할 경우 내용이 좀더 이해가 된다.
` for (num in seq2)` 를 이용하여 시퀀스 내부를 iterator할 경우 `println("Generating first")` 호출되고, yield(1)에서 멈추게 된다, 이때
main function에서의 `println("The next number is $num")`호출 될 경우 num의 값을 알아야 하기 때문에 seq2에서 `yield(1)` 실행되고,
아래 `println("Generating second")`가 실행 된 뒤 `yield(2)`에서 멈춘다.

Yield 함수는 중단 함수의 역할을 한다.

따라서 아래 fibonacci 코드를 보면 재밌는 걸 확인할 수 있다.

```kotlin

import java.math.BigInteger

val fibonacci: Sequence<BigInteger> = sequence {
    var first = 0.toBigInteger()
    var second = 1.toBigInteger()
    while (true) {
        yield(first)
        val temp = first
        first += second
        second = temp
    }
}

fun main() {
    println(fibonacci.take(10).toList())
}
// [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
```

피보나치 수열의 while문은 무한으로 생성되는 것이지만, yield함수를 사용함으로써, 원하는 만큼을 생성하고 중단할 수 있다.

# 중단은 어떻게 작동할까?

중단함수는 코틀린 코루틴의 핵심이다.
코루틴은 중단되었을 때 Continuation 객체를 반환한다. 이 객체는 게임을 저장하는 것과 비슷하다. Continuation을 이용하면 멈췄던 곳에서 다시 코루틴을 실행할 수 있다.

코루틴은 다른 스레드에서 시작할 수 있고, 컨티뉴에이션 객체는 직렬화와 역직렬화가 가능하며 다시 실행될 수 있다.

## 재개

중단 함수는 말 그대로 코루틴을 중단할 수 있는 함수이다.  
이는 중단 함수가 반드시 코루틴(또는 다른 중단 함수)에 의해 호출되어야 함을 의미한다.

```kotlin
suspend fun main() {
    println("Before")

    println("After")
}
// Before
// After
```

```kotlin
suspend fun main() {
    println("Before")

    suspendConroutine<Uint> { continuation ->
        println("Before too")
    }
    println("After")
}
// Before
// Before too

```

컨티뉴에이션은 suspendCoroutine 호출된 지점의 람다식으로 객체 인자로 받는다.

다른 함수 곧바로 호출하는 함수는 let, apply, useLines처럼 코틀린에서 흔히 볼 수 있다.

컨티뉴에이션 객체를 이용하면 코루틴을 중단한 후 곧바로 실행도 가능하다.

```kotlin
suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { continuation ->
        continuation.resume(Unit)
    }
    println("After")
}
/*
Before
After
 */
```

After가 호출되는 것은 컨티뉴에이션에서 resume을 호출했기 때문이다.

## 값으로 재개하기

suspendCoroutine을 호출할 때 컨티뉴에이션 객체로 반환될 값의 타입을 지정할 수 있다.

```kotlin
val i: Int = suspendCoroutine<Int> { cont ->
    cont.resume(42)
}

println(i) // 42
//    ...
```

코루틴에서는 값으로 재개하는 것이 자연스럽다.  
API를 호출해 네트워크 응답을 기다리는 것처럼 특정 데이터를 기다리려고 중단하는 상황은 자주 발생한다. 스레드는 특정 데이터가 필요한 지점까지 비즈니스 로직을 수행한다.  
이후 네트워크 라이브러리를 통해 데이터를 요청한다. 코루틴이 없다면 스레드는 응답을 기다리고 있을 수 밖에 없다. (1)스레드를 생성하는 비용이 많이 들기도하며, (2) 안드로이드의 메인 스레드처럼 중요하다면
스레드가 가만히 대기하는 것은 엄청난 낭비이다.

코루틴이 있으면 중단함과 동시에 "데이터를 받고 나면, 받은 데이터를 resume 함수를 통해 보내줘" 라고 컨티뉴에이션 객체를 통해 라이브러리에 전달한다.

## 예외로 재개하기

작성하는 함수들은 값을 반환하거나 예외를 던진다. suspendCoroutine도 마찬가지이다.
resume이 호출될 때 suspendCoroutine은 인자로 들어온 데이터를 반환한다. resumeWithException이 호출되면 중단된 지점에서 예외를 던진다.

```kotlin
suspend fun main() {
    try {
        suspendCoroutine<Unit> { cont ->
            cont.resumeWithException(MyException())
        }
    } catch (e: MyException) {
        println("Caught!")
    }
} // Caught!
```

## 함수가 아닌 코루틴을 중단시킨다

중단 함수는 코루틴이 아니고, 단지 코루틴을 중단할 수 있는 함수라고 할 수 있다.  
변수에 컨티뉴에이션 객체를 저장하고, 함수를 호출한 다음에 재개하는 상황을 보자.

```
var continuation: Continuation<Unit>? = null

suspend fun suspendAndSetContinuation() {
    suspendCoroutine<Unit> { cont ->
        continuation = cont
    }
}


suspend fun main() {
    println("Before")

    suspendAndSetContinuation()
    continuation?.resume(Unit)

    println("After")
}

```

위 코드를 실행하면 "Before"가 호출되는 것만 볼 수 있으며, 다른 스레드나 다른 코루틴으로 재개하지 않으면 프로그램은 실행된 상태로 유지된다.

```
var continuation2: Continuation<Unit>? = null

suspend fun suspendAndSetContinuation2() {
    suspendCoroutine<Unit> { cont ->
        continuation2 = cont
    }
}

suspend fun main() = coroutineScope {
    println("Before")

    launch {
        delay(1000)
        continuation2?.resume(Unit)
    }
    suspendAndSetContinuation2()
    println("After")
}
// Before
// 1초 뒤
// After
```

# 코루틴의 실제 구현

코루틴의 동작 과정

- 중단 함수는 함수가 시작할 때와 중단 함수가 호출되었을 때 상태를 가진다는 점에서 상태 머신 (state machine)과 비슷하다.
- 컨티뉴에이션(continuation) 객체는 상태를 나타내는 숫자와 로컬 데이터를 가지고 있다.
- 함수의 컨티뉴에이션 객체가 이 함수를 부르는 다른 함수의 컨티뉴에이션 객체를 장식(decorate) 한다. 그 결과 모든 컨티뉴에이션 객체는 실행을 재개하거나 재개된 함수를 완료할 때 까지 사용되는 콜 스택으로
  사용된다.

## 컨티뉴에이션 전달 방식

중단 함수가 구현될 수 있는 수 많은 방법 중에서 코툴린 팀은 컨티뉴에이션 전달 방식(continuation-passing style)을 선택했다.

CPS
> Continuation Passing Style (CPS)은 함수형 프로그래밍에서 사용되는 스타일 중 하나로, 비동기 작업이나 제어 흐름을 명시적으로 표현할 때 유용합니다.

javascript 예제

```javascript
const fs = require('fs');

function readFileAsync(filename, callback) {
    fs.readFile(filename, 'utf8', (err, data) => {
        if (err) {
            callback(err, null);
        } else {
            callback(null, data);
        }
    });
}

readFileAsync('example.txt', (err, data) => {
    if (err) {
        console.error('Error:', err);
    } else {
        console.log('File data:', data);
    }
});

```

Java 예제

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            try {
                String content = new String(Files.readAllBytes(Paths.get("example.txt")));
                System.out.println("File data: " + content);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });

        future.exceptionally(ex -> {
            System.err.println("Error: " + ex.getMessage());
            return null;
        });

        // 다른 작업 수행 가능
    }
}

```

### CPS와 AKKA는 유사성이 있는가?

> Akka는 비동기 및 병렬 프로그래밍을 지원하는 도구로서, CPS와 유사한 개념을 일부로 사용할 수 있습니다. Akka는 Actor 모델을 기반으로 한 분산 시스템 및 병렬 처리를 위한 라이브러리입니다.  
> Akka에서 액터(Actors)는 각각의 독립된 상태와 메시지 처리 루틴을 가지며, 이들 간의 통신은 비동기 메시지 전달을 통해 이루어집니다. 이런 구조는 CPS와 유사한 개념을 가지고 있으며, 메시지를 처리하는
> 함수를 통해 결과를 반환하거나 다음 단계로 흐름을 전달할 수 있습니다.  
> Akka는 다양한 동시성 및 병렬성 문제를 다루는 데 유용하며, 분산 시스템에서 확장성을 제공하는 데 도움이 됩니다. Akka의 주요 구성 요소 중 하나인 "Futures" 및 "Promises"를 사용하여
> 비동기 작업을 처리할 수도 있습니다.  
> 따라서 Akka는 비동기 및 병렬 프로그래밍을 위한 도구로 사용되며, CPS와 유사한 개념을 효과적으로 활용할 수 있는 옵션 중 하나입니다.

### 함수형 프로그래밍의 Tail Recursion과 CPS는 유사하지 않는가?

> Tail recursion과 Continuation Passing Style (CPS)은 함수형 프로그래밍에서 다른 개념이지만, 그들 간에 유사성이 있을 수 있습니다.
>> Tail Recursion:Tail recursion은 함수 호출이 자기 자신을 다시 호출할 때, 그 호출이 현재 함수의 마지막 동작인 경우를 나타냅니다. 이 경우, 컴파일러나 인터프리터는 추가 메모리 스택을
> > 생성하지 않고 현재 스택 프레임을 재사용하여 재귀 함수를 최적화합니다. 이를 통해 스택 오버플로우를 방지하면서도 재귀적인 알고리즘을 구현할 수 있습니다.

> > Continuation Passing Style (CPS):CPS는 비동기 또는 제어 흐름을 명시적으로 표현하기 위한 프로그래밍 스타일입니다. 이 스타일에서 함수는 결과 대신 다음 동작을 수행하는 콜백 함수를
> > 인자로 받습니다. 이러한 콜백 함수를 "continuation"이라고 부릅니다. CPS는 보통 비동기 코드나 예외 처리, 제어 흐름 관리 등에 사용됩니다.
> > Tail recursion과 CPS 모두 호출 스택에 대한 고려사항이 있는데, Tail recursion은 재귀 호출의 최적화를 위한 것이며, CPS는 제어 흐름의 표현과 관련이 있습니다.

그러나 Kotlin Coroutine에서 사용한 continuation 객체와 Tail recursion의 최적화와는 직접적인 연관성이 적을 수 있습니다. Kotlin Coroutine의 continuation
객체는 현재 실행 중인 코루틴의 상태와 실행을 일시 중단하거나 재개하는 데 사용됩니다. 이는 스레드와 관련된 스택 오버플로우 문제를 해결하고, 비동기 코드를 동기식처럼 작성하는 데 도움이 됩니다.

따라서 CPS와 Kotlin Coroutine의 continuation 객체가 유사한 개념으로 이해되긴 어렵지만, 모두 비동기 또는 제어 흐름을 다루는 데 도움을 주는 프로그래밍 기법입니다. Tail
recursion은 다른 측면에서 최적화된 재귀적인 함수 호출을 다루는 데 사용됩니다.

내용을 보니 서로 다른 것이라고 생각됨

## 상태를 가진 함수

함수가 중단된 후에 다시 사용할 지역 변수나 파라미터와 같은 상태를 가지고 있다면, 함수의 컨티뉴에이션 객체에 상태를 저장해야 한다.

위에서 살펴본 코드를 decompile해보면 다음과 같은 source를 볼 수 있다.

```java

@Metadata(
        mv = {1, 9, 0},
        k = 2,
        d1 = {"\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\b\u001a\u000e\u0010\u0007\u001a\u00020\u0002H\u0086@¢\u0006\u0002\u0010\b\u001a\u000e\u0010\t\u001a\u00020\u0002H\u0086@¢\u0006\u0002\u0010\b\"\"\u0010\u0000\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\u0001X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0003\u0010\u0004\"\u0004\b\u0005\u0010\u0006¨\u0006\n"},
        d2 = {"continuation2", "Lkotlin/coroutines/Continuation;", "", "getContinuation2", "()Lkotlin/coroutines/Continuation;", "setContinuation2", "(Lkotlin/coroutines/Continuation;)V", "main", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "suspendAndSetContinuation2", "code"}
)
public final class _8_coroutine_scopeKt {
    @Nullable
    private static Continuation continuation2;

    @Nullable
    public static Continuation getContinuation2() {
        return continuation2;
    }

    public static void setContinuation2(@Nullable Continuation var0) {
        continuation2 = var0;
    }

    @Nullable
    public static Object suspendAndSetContinuation2(@NotNull Continuation $completion) {
        SafeContinuation var2 = new SafeContinuation(IntrinsicsKt.intercepted($completion));
        Continuation cont = (Continuation) var2;
        int var4 = false;
        continuation2 = cont;
        Object var10000 = var2.getOrThrow();
        if (var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended($completion);
        }

        return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
    }

    @Nullable
    public static Object main(@NotNull Continuation $completion) {
        Object var10000 = CoroutineScopeKt.coroutineScope((Function2) (new Function2((Continuation) null) {
            // $FF: synthetic field
            private Object L$0;
            int label;

            @Nullable
            public Object invokeSuspend(@NotNull Object $result) {
                Object var4 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                String var3;
                switch (this.label) {
                    case 0:
                        ResultKt.throwOnFailure($result);
                        CoroutineScope $this$coroutineScope = (CoroutineScope) this.L$0;
                        var3 = "Before";
                        System.out.println(var3);
                        BuildersKt.launch$default(
                                $this$coroutineScope,
                                (CoroutineContext) null,
                                (CoroutineStart) null,
                                (Function2) (new Function2((Continuation) null) {
                                    int label;

                                    @Nullable
                                    public Object invokeSuspend(@NotNull Object $result) {
                                        Object var4 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                        switch (this.label) {
                                            case 0:
                                                ResultKt.throwOnFailure($result);
                                                this.label = 1;
                                                if (DelayKt.delay(1000L, this) == var4) {
                                                    return var4;
                                                }
                                                break;
                                            case 1:
                                                ResultKt.throwOnFailure($result);
                                                break;
                                            default:
                                                throw new IllegalStateException(
                                                        "call to 'resume' before 'invoke' with coroutine");
                                        }

                                        Continuation var10000 = _8_coroutine_scopeKt.getContinuation2();
                                        if (var10000 != null) {
                                            Continuation var2 = var10000;
                                            Unit var3 = Unit.INSTANCE;
                                            Result.Companion var10001 = Result.Companion;
                                            var2.resumeWith(Result.constructor - impl(var3));
                                        }

                                        return Unit.INSTANCE;
                                    }

                                    @NotNull
                                    public Continuation create(
                                            @Nullable Object value,
                                            @NotNull Continuation completion
                                    ) {
                                        Intrinsics.checkNotNullParameter(completion, "completion");
                                        Function2 var3 = new <anonymous constructor > (completion);
                                        return var3;
                                    }

                                    public final Object invoke(Object var1, Object var2) {
                                        return (( < undefinedtype >) this.create(var1, (Continuation) var2)).
                                        invokeSuspend(Unit.INSTANCE);
                                    }
                                }),
                                3,
                                (Object) null
                        );
                        this.label = 1;
                        if (_8_coroutine_scopeKt.suspendAndSetContinuation2(this) == var4) {
                            return var4;
                        }
                        break;
                    case 1:
                        ResultKt.throwOnFailure($result);
                        break;
                    default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }

                var3 = "After";
                System.out.println(var3);
                return Unit.INSTANCE;
            }

            @NotNull
            public final Continuation create(@Nullable Object value, @NotNull Continuation completion) {
                Intrinsics.checkNotNullParameter(completion, "completion");
                Function2 var3 = new <anonymous constructor > (completion);
                var3.L$0 = value;
                return var3;
            }

            public final Object invoke(Object var1, Object var2) {
                return (( < undefinedtype >) this.create(var1, (Continuation) var2)).invokeSuspend(Unit.INSTANCE);
            }
        }), $completion);
        return var10000 == IntrinsicsKt.getCOROUTIN
```

컴파일된 클래스 파일을 확인해보면 클래스 내에 label과 val로 상태값을 저장하기 위한 내용이 만들어지는 것을 확인할 수 있다.

1개의 동작이 완료가 되면 label 값을 변경하고, 상태를 저장 한 뒤, 다음번 실행될 때 label 값을 확인하고, 다음 작업을 진행하는 형태로 동작하는 것 같다.

내부적으로 이런 형식으로 만들어주고, 스레드가 할당 받을 때 마다 재개 할 수 있도록 만들어 주는 것 같다.

책 예제 코드는 아래와 같다.

```kotlin
fun myfunction(continuation: Continuation<Unit>): Any {
    val continuation = continuation as? MyFunctionContinuation
        ?: MyFunctionContinuation(continuation)
    var counter = continuation.counter

    if (continuation.label == 0) {
        println("Before")
        counter = 0
        continuation.counter = counter
        continuation.label = 1
        if (delay(1000L, continuation) == COROUTINE_SUSPENDED) { // delay 함수 변경된 듯
            return COROUTINE_SUSPENDED
        }
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

```

## 값을 받아야 중단되는 함수

이 부분은 Continuation 내에 설정한 변수가 추가되고, 그 값이 셋팅되는 과정이 좀더 있는 것으로 보인다.

## 콜 스택

호출 스택(Call Stack)은 프로그램이 함수 호출을 관리하는 데이터 구조로, 함수 호출의 실행 흐름을 추적하고 함수의 상태를 저장합니다. 호출 스택은 스택(Stack) 자료 구조의 형태를 띠며, 함수 호출을
순차적으로 쌓고 실행이 완료되면 역순으로 꺼내어 처리합니다. 여기에 대한 간단한 설명은 다음과 같습니다:

함수 호출:

프로그램이 함수를 호출할 때, 해당 함수의 호출 정보(함수 이름, 매개변수 값 등)가 호출 스택에 추가됩니다.
스택 구조:

호출 스택은 스택(Stack) 자료 구조로 동작합니다. 이는 "Last-In, First-Out" (LIFO) 구조로, 마지막에 추가된 요소가 가장 먼저 제거됩니다.
쌓기 및 제거:

함수 호출이 중첩되면 호출 스택에 여러 개의 함수 호출이 쌓입니다. 현재 실행 중인 함수가 완료되면 해당 함수의 정보가 스택에서 제거됩니다.
재귀 호출:

함수가 자기 자신을 호출하는 재귀 호출을 할 때, 스택에 같은 함수가 여러 번 중첩될 수 있습니다. 재귀 호출은 스택의 깊이(depth)를 증가시키므로, 스택 오버플로우를 방지하기 위해 주의해야 합니다.
함수 실행 순서:

호출 스택은 함수 호출의 실행 순서를 엄격하게 추적합니다. 가장 위에 있는 함수 호출이 현재 실행 중인 함수이며, 그 아래에 있는 호출들은 대기 중인 함수 호출입니다.
함수의 로컬 변수와 상태:

각 함수 호출은 고유한 로컬 변수와 상태를 가지며, 이 정보는 호출 스택에 저장됩니다. 함수 호출이 완료되면 해당 함수의 로컬 변수 및 상태도 제거됩니다.
호출 스택은 프로그램의 실행 흐름을 추적하고 함수 호출의 순서를 관리하기 위한 중요한 요소입니다. 오류 디버깅 및 함수 호출의 관리를 도와줍니다.

* 콜 스택은 저장공간이 제한되어 있어서 이 공간을 모두 사용하면 StackOverFlowError가 발생한다.

코루틴을 중단하면 스레드를 반환해 콜 스택에 있는 정보가 사라지는데, 다시 스레드를 할당받아 이 코루틴을 재개할 때 콜 스택을 사용할 수 없게 된다.
대신, 컨티뉴에이션 객체가 콜 스택의 역할을 대신한다. 컨티뉴에이션 객체는 중단이 되었을 때 상태(label)와 함수의 지역 변수 파라미터(필드, 그리고 중단 함수를 호출한 함수가 재개될 위치 정보를 가지고 있다.

예제가 정확히 동작하지 않음.. -_-; 13번 호출이라니 아닌데?

## 중단 함수의 성능

비용이 생각보다 크지 않다. 함수를 상태로 나누는 것은 숫자를 비교하는 마늠 쉽게 설계되어 있고, 실행점이 변하는 비용도 거의 들지 않는다. 컨티뉴에이션 저장하는 것도 간단하다. 따라서 중단 함수의 성능에 걱정하지
않아도 된다.

