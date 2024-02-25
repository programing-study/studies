# 일반적인 사용 예제

이 책에서는 일반적으로 사용되는 예제들의 사례를 들면서 백엔드, 안드로이드에서 널리 사용되는 패턴을 소개했다.

* 데이터 어뎁터 계층
* 도메인 계층
* 표현/API/UI 계층

콜백함수 형태로 사용하는 방법 [suspendCancellableCoroutine](2-week.md#suspendcancellablecoroutine)

```Kotlin
suspend fun requestNews(): News {
    return suspendCancellableCoroutine<News> { cont ->
        val call = requestNewsApi { news ->
            cont.resume(news)
        }
        cont.invokeOnCancellable {
            call.cancel()
        }
    }
}
```

## 블록킹 함수

어쩔 수 없이 블록킹 함수를 사용해야 하는 라이브러리도 많이 볼 수 있다. 일반적인 중단 함수에서는 블록킹 함수를 절대 호출하면 안된다.

안드로이드에서 Dispatchers.Main의 스레드가 블로킹되면 전체 애플리케이션 실행이 멈추가 된다. Dispatchers.Default의 스레드를 블로킹하면 프로세서를 효율적으로 사용하지 못하게 된다.
따라서 디스패처를 명시하지 않고 블로킹 함수를 호출하면 절대 안된다.

블로킹 함수를 호출하려면 withContext를 사용해 디스패처를 명시해야 한다. 대부분의 경우 애플리케이션에서 저장소를 구현할 때는 Dispatchers.IO를 사용하면 된다.

```kotlin
class DiscSaveRepository {
    private val discReader: DiscReader
} {
    override suspend fun loadSave(name: String): SaveData =
        withContext(Dispatchers.IO) {
            discReader.read("save/$name")
        }
}
```

IO 디스패처를 사용할 경우, 스레드가 64개로 제한되어 있기 때문에 백엔드나 안드로이드에서 충분하지 않을 수 있다는 점을 알고 있어야 한다.
모든 요청이 블로킹 함수를 호출하고 초당 수천 개의 요청을 처리해야 한다면 64개의 스레드를 기다리는 요청이 급격히 증가하게 된다. 이런 경우에는 Dispatchers.IO의 limitedParallelism을
사용해 64개보다 더 많은 스레드를 활용하는 새로운 디스패처를 만드는 방법도 고려해 볼 수 있다.  
[limitedParallelism](2-week.md#기본-디스패처-제한하기)

함수를 호출하는 코루틴이 너무 많아 상당한 수의 스레드가 필요하다고 예상될 때, Dispatchers.IO와 독립적인 스레드 제한을 가진 디스패처를 사용해야 한다. Dispatchers.IO의 스레드를 블로킹하여
의도치 않게 어떤 작업이 대기하게 되는 경우를 방지하기 위함이다.  
너무 작은 수로 정하면 코루틴이 다른 코루틴을 기다리는 경우가 생길 수 있다. 너쿠 큰 수로정하면 활성화된 스레드로 인해 할당된 메모리가 CPU 실행시간도 길어기게 된다.

## 적정 스레드 구하는 방법

하드웨어 코어 수 고려하기:
일반적으로, 사용 가능한 CPU 코어 수에 기반하여 스레드 수를 결정하는 것이 좋습니다. 이론적으로, CPU 코어 하나당 하나의 스레드가 최적의 성능을 발휘할 수 있습니다. 따라서, 사용 가능한 코어 수를 확인하고,
해당 수에 비례하여 스레드 수를 설정할 수 있습니다.

CPU 바운드와 I/O 바운드 작업 구분하기:

CPU 바운드 작업의 경우, 더 많은 스레드를 추가한다고 해서 성능이 크게 향상되지 않습니다. 이런 경우에는 CPU 코어 수보다 많은 스레드를 생성하는 것이 역효과를 낼 수 있습니다.
I/O 바운드 작업의 경우, 스레드가 대기 상태에 있을 때 다른 스레드가 CPU를 사용할 수 있으므로, CPU 코어 수보다 많은 스레드를 할당하는 것이 유리할 수 있습니다.
실험을 통한 조정:
적정 스레드 수는 애플리케이션에 따라 크게 달라질 수 있으므로, 다양한 설정을 실험해보고 성능을 모니터링하는 것이 중요합니다. 성능 테스트를 통해 애플리케이션이 가장 효율적으로 실행되는 스레드 수를 찾을 수
있습니다.

공식 사용하기:
적정 스레드 수 =
$사용 가능한 코어 수 \times (1 + \frac{W}{C})$

환경 고려하기:
실행 환경에 따라 최적의 스레드 수가 달라질 수 있습니다. 예를 들어, 다른 많은 애플리케이션이 동시에 실행되고 있는 서버에서는 사용 가능한 리소스가 제한될 수 있습니다.

I/O 대기 시간이 80%의 시간을 차지한다고 했을 때, 이것이 왜 대기 시간이 계산 시간보다 4배 더 길다고 여겨지는지 설명하겠습니다. 이 비율을 이해하기 위해 전체 작업 시간을 100%라고 가정하고, 이 중에서
I/O 대기에 소요되는 시간과 실제 계산에 사용되는 시간을 구분해 보겠습니다.

여기서 I/O 대기 시간(W)이 80%라고 하면, 남은 계산 시간(C)은 100% - 80% = 20%입니다. 이제 W와 C의 비율을 비교해야 합니다.

W가 80%이고, C가 20%이므로, W는 C보다 얼마나 더 긴지 비율로 표현해 봅시다:

적정 스레드 수 =
$8 \times (1 + \frac{80\%}{20\%}) = 8 \times (1 + 4) = 8 \times 5 = 40$

이 계산은 I/O 대기 시간이 계산 시간보다 4배 더 길다는 것을 의미합니다. 즉, 프로그램이 I/O 작업을 기다리는 시간이 실제 계산을 수행하는 시간보다 4배 더 많다는 것을 나타냅니다. 이러한 비율을 이해하고
고려하는 것은 리소스 사용과 스레드 할당을 최적화하는 데 중요합니다.

## Flow 감지하기

중단 함수는 하나의 값을 생성하고 가져오는 과정을 구현하기에 적합합니다. 하지만 여러 개의 값을 다루는 경우에는 Flow를 사용해야 한다.

API를 통해 하나의 값을 가져올 때는 중단 함수를 사용하는 것이 가장 좋다. 하지만 웹소켓(WebSocket)을 설정하고 메시지를 기다릴 때는 Flow를 사용해야 한다.  
플로우를 만들 때는 `callBackFlow`를 사용한다. 플로우 빌더의 끝에는 `awaitClose`를 반드시 넣어줘야 한다.

```kotlin
fun listenMessage(): Flow<List<Message>> = callbackFlow {
    socket.on("NewMessage") { args ->
        trySendBlocking(it.toString())
    }
    awaitClose { removeTextChangedListener(watcher) }
}
```

## 도메인 계층

도메인 계층에는 코루틴 스코프 객체에서 연산을 처리하거나 중단 함수를 노출시키는 건 절대 안된다. 스코프 객체에서 코루틴을 시작하는 건 아래 있는 표현 계층이 담당해야 한다. 도메인 계층에서 코루틴을 시작하려면
코루틴 스코프 함수를 써야 한다.

```kotlin
class NetworkUserRepository(
    private val api: UserApi,
) : UserRepository {
    override suspend fun getUser(): User =
        api.getUser().toDomainUser()
}

class NetworkNewsService(
    private val newRepo: NewsRepository,
    private val settings: SettingsRepository,
) {
    suspend fun getNews(): List<News> = newsRepo
        .getNews()
        .map { it.toDomainNews() }
    suspend fun getNewsSummary(): List<News> {
        val type = settings.getnewsSummaryType()
        return newsRepo.getNewsSummary(type)
    }
}
```

### 동시 호출

두 개의 프로세스를 병렬로 실행하려면 함수 본체를 coroutineScope로 래핑하고 내부에서 async 빌더를 사용해 각 프로세스를 비동기로 실행해야 한다.

```kotlin
suspend fun produceCurrentUser(): User = coroutineScope {
    val profile = async { repo.getProfile() }
    val friends = async { repo.getFriends() }
    User(profile.await(), friends.await())
}
```

그 밖에 사용할 때 주의할 점 등등 작성되며 마무리 되었다.

