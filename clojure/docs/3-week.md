덧셈 함수

벡터 안에 요소 찾기

과제를 적어주면 그거에 맞춰서 코드를 짜온다.

두 문자열을 받아 CONCAT 하는 함수 생성 (p1 “hello” “world”) => “helloworld”

```
(defn concat1 [str1, str2] ( str str1 str2))
```

문자열의 컬렉션을 받아 요소들을 CONCAT 하는 함수 생성 (p2 ‘(“my” “ name” “ is” “ what”)) => “my name is what”

```
(defn p2 [input]
(loop [in input out ""]
(if (empty? in)
(str out)
(recur (next in)
(str out (first in) " ")
))))

```

두 정수를 받는 이항함수와 정수 2개를 받아 이항함수의 호출 결과를 리턴하는 함수 생성 (p3 + 1 3) => 4

```
(defn p3 [opcode num1 num2] (opcode num1 num2))
```

피보나치 수열을 반환하는 함수 (p4 7) -> 13

```
(defn p4 [num]
(if (= num 0) 0
(let [fibona (fn [num1 num2 count]
(if (= count num)
    num2(recur num2 (+ num1 num2) (+ count 1)))
    )]
(fibona 0 1 1))))

```

팩토리얼 계산하는 함수 (p5 4) -> 24

집합과 진위함수를 받아 진위함수가 참값을 리턴한 요소들의 리스트를 반환하는 함수
두 인자를 받아 무한 정수 배열에서 1번째 인자로 무한 정수 배열을 제한하고 두 번째 인자만큼 제한된 배열을 반복하는 함수 (p6 9 3) -> 1 2 3 4 5 6 7 8 1 2 3 4 5 6 7 8 1 2 3 4
5 6 7 8

3주차 스터디 때 같이 풀 내용 : https://4clojure.oxal.org/

유용한 사이트 https://practical.li/clojure/coding-challenges/
함수 레퍼런스 https://clojuredocs.org/clojure.core/and

```
(fn [input]
    (loop [in input out ""]
        (if (empty? in)
            out
        (recur (rest in) (first in))
        )
    )
)
```

### 11/9 - 문제풀이 진행 중

### 11/16 - 문제풀이 진행 중

42번까지 풀어오기

42번까지 다 풀면 43번도 풀기

### 12/7 - 문제 풀면서 헷갈렸던 부분 정리

문제 풀면서 잘 이해가 안된 부분을 정리해보자.

익숙하지 않은 내부 함수 정의  
let, 다른 fn을 선언해서 해야하는지?

loop, recur 문을 제외한 내부 정의 후 작성은 매우 드물었다.  
-> 내장함수를 찾아보고 하지 않았나
부분적용함수(partial)들을 익혔지만, 사용하진 못함.  
내장함수들을 몰라서 직접 구현하느라 힘듬  
reference 문서에 description 단어가 익숙하지 않음  
if문 작성시 else문 괄호() 위치를 계속 틀림  
함수형 프로그래밍을 하다보니, 재귀 함수를 기본으로 하고 이때는 loop, recur 조합으로 많이 사용  
하지만 재귀탈출 조건이 1개 이상일 경우 cond를 이용해서 사용함.  
(마치 switch case 또는 scala match case 느낌)

익명 함수는 중첩 정의가 불가능(정확히는 파라미터 이름이 겹치기 때문에 중첩이 어려움)  
익명 함수의 파라미터는 여러개가 가능하며 %1 %2.. %n 으로 정의 가능  
#(%1 %2 #(%3 % %4))  
1,2,3,4 첫번째 익명함수 parameter, %는 두번째 익명함수 parameter  
apply 함수는 인자로 받은 함수를 뒤에 오는 배열 요소에 각각 적용시켜주는 기능을 제공  
배열처리를 하는 것에 기인, lisp

```
fn plus1(a) = a + 1
fn plus5(a b c d e) = a + b + c + d + e
fn plusApply(arr) = apply(plus1 arr)
```

loop, recur 의 [ ] 쪽 인자 선언이 헛갈리는 부분이 많았음  
cond 의 경우 매칭케이스가 없는 경우(즉 매칭되어도 아무런 특별 처리가 없는 경우)에도 아무런 처리를 하지 않는 표현식을 써 주어야 했음  
loop, recur 에서는 recur 가 반드시 꼬리재귀여야 하였으며 중간 재귀가 들어갈 경우엔 함수 이름을 지정해서 함수 이름으로 호출하여 재귀를 구현해야 함  
특이한 함수들을 많이 봄 interleave, partition, drop, take, apply  
괄호를 잘 닫는 부분과 함수가 전위표현식으로 되어 있는 부분이 자주 헛갈림 (+ 3 5) (- 4 3) O  
(rest [1 2 3 4 5] ) OK  
rest ( [1 2 3 4 5] ) Not OK  
특히 뺄셈은 연산 순서가 중요하여 위치 선정이 중요함 (- 4 3) != (- 3 4)  
가변 배열은 &로 정의

```
fn a(...n)
(defn f1 [a b c d & colls] ..)
(f1 1 2 3 4 ) -> colls 는 빈 배열
(f1 1 2 3 4 5 6 ) -> colls ‘(5 6)
(f1 1 2 3 4 5 ) -> colls ‘(5) <- apply 함수가 유용하게 쓰인다
```

colls 가 배열이다보니까 배열에 적용할 수 있는 함수를 그대로 사용 가능

```
( filter #( > % 1) colls )
( map #(+ % 1) colls)
```

별게 아니지만, 새로운 언어를 배울 때, 이런 함수형 언어는 특히 생각을 많이 하게 되는 것 같음
물론 덕분에 내가 아직도 많이 부족하다는 것을 많이 느낌.

대신, 이런 방법으로 해결하려는 과정을 갖다보면 어느 순간 좀더 나은 방향으로 코드를 작성할 수 있지 않을까? 라는 생각이 듦.

라이브러리 공부하는게 아닌가 싶은 생각도 듦.

### 12/14 (수)

### 12/21은 스터디 쉼

### 다음 스터디 12월 마지막 주

Optional 유사 구현 with kotlin

```
map function
filter function
get() funtion
orElse() function
of()

```

```
// 의사 코드
class Optional<T : Any>(
val data: T?
) {

fun map
}

// 고려해야 할 내용
// Optional.of(3)
.map(it -> it + 3) // Optional<Int>
.map(it -> it + 4) // Optional<Int>
.get() //-> 10

// Optional.of(3)
.map(it -> it + 3) // Optional<Int>
.map(it -> it + 4)  // Optional<Int>
.map(it -> it.toString()) // Optional<String>
.get() //-> “10”

// Optional.of(3)
.map(it -> it + 3) // Optional<Int>
.map(it -> it + 4)  // Optional<Int>
.filter(it -> it < 10) // 들어 있는 값이 null
.get() //-> null

// Optional.of(3)
.map(it -> it + 3) // Optional<Int>
.map(it -> it + 4)  // Optional<Int>
.filter(it -> it < 10) // 들어 있는 값이 null
.orElse(34) //34

// Optional.of(null)
.map(it -> it + 3) // Optional<Int>
.map(it -> it + 4)  // Optional<Int>
.filter(it -> it < 10) // 들어 있는 값이 null
.orElse(34) //34


```

추가적
equals

```
val o = Optional.of(3)

o.equals(3) // false
o.equals(Optional.of(3)) //true
o.equals(Optional.of(null)) //false
Optional.of(null).equals(Optional.of(null)) // false

추가적으로 kotlin 은 함수를 일급 객체로 취급한다. 따라서 이를 Optional 에도 동일하게 개념을 확장한다.
c
Optional 함수형 Monad, Functor


//함수의 일급 객체 취급에 의한 optional 동작 확장
// 제약 java optional 쓰지 말기. 11

1차 목표 : 단항 함수로 한정

// 의사 코드
val o = Optional.of(  (_) -> (_ + 3) )

//define apply function
// fun apply(Optional<Function>, Optional.of(Value))
apply(o, Optional.of(3)) -> Optional.of(6)
apply(o, Optional.of(null)) -> Optional.of(null)



```

> optional class java doc
https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html
>> A container object which may or may not contain a non-null value. If a value is present, isPresent() returns true. If
> > no value is present, the object is considered empty and isPresent() returns false.
> > Additional methods that depend on the presence or absence of a contained value are provided, such as orElse() (returns a
> > default value if no value is present) and ifPresent() (performs an action if a value is present).
> > This is a value-based class; use of identity-sensitive operations (including reference equality (==), identity hash
> > code, or synchronization) on instances of Optional may have unpredictable results and should be avoided.
> > API Note:
> > Optional is primarily intended for use as a method return type where there is a clear need to represent "no result," and
> > where using null is likely to cause errors. A variable whose type is Optional should never itself be null; it should
> > always point to an Optional instance.
> > Since:
> > 1.8

> > 한글 번역
> > null이 아닌 값을 포함하거나 포함하지 않을 수 있는 컨테이너 개체입니다. 값이 있으면 isPresent()는 true를 반환합니다. 값이 없으면 객체가 비어 있는 것으로 간주되고 isPresent()는
> > false를 반환합니다.
> > orElse()(값이 없으면 기본값 반환) 및 ifPresent()(값이 있으면 작업 수행)과 같이 포함된 값의 유무에 따라 달라지는 추가 메서드가 제공됩니다.

이것은 값 기반 클래스입니다. 선택적 인스턴스에서 ID에 민감한 작업(참조 같음(==), ID 해시 코드 또는 동기화 포함)을 사용하면 예측할 수 없는 결과가 발생할 수 있으므로 피해야 합니다.

API 참고:
옵셔널은 주로 "결과 없음"을 명시해야 하고 null을 사용하면 오류가 발생할 가능성이 있는 메서드 반환 유형으로 사용하기 위한 것입니다. 유형이 선택적인 변수는 그 자체가 널이 아니어야 합니다. 항상
Optional 인스턴스를 가리켜야 합니다.  
부터:
1.8

12/28 (수) - 값이 다건에 경우? Flux??

Flux_fox<T> // 목적은 다건의 값을 다루를 수 있는 문맥(context)

기존의 했던 optional 은 단건의 값을 다루는 문맥(context) <- 값이 있거나 없을 수 있다라는 맥락을 표현하는 부가정보가 있는 건
ddd

```
//의사 코드Flux.of(1, 2, 3, 4, 5)  < - 5건이 들어 있는 문맥
Flux.of(listOf(1, 2, 3, 4, 5)) < 갑ㅇㅇ

Flux.of(listOf(1, 2, 3, 4, 5)) <- 이거는 단건이 들어있는 문맥
Flux.of(n

Flux.of(null, 2, 3, null, 3, 4, 4) 은 고려하지 않는 걸로 자유
Flux.of(2,2,2,2,2,2)

Flux.of(1, 2, 3, 4).map { it + 3 } // Flux.of(4, 5, 6, 7)

Flux.of(1, 2, 3, 4).filter { it > 3} // Flux.of(4)
Flux.of(1, 2, 3, 4).filter { it > 4} // Flux.of()

Flux.of(listOf(1, 2, 3, 4)).flatmap { it.toFlux() } // Flux.of(1, 2, 3, 4)

Flux.of( Optional.of(1), Optional.of(2), Optional.of(3) )
Optional.of( Flux.of ( 1, 2, 3, 4) )

Optional.of( Flux.of ( 1, 2, 3, 4) )
.map { it.filter { it > 3 }  } // Optional.of ( Flux.of(4) )

Flux.of( 1, 2, 3, 4 ). get()
// list : [1 2 3 4]
Flux.of().get()
// emptyList()

//고려하지 않는 사항x
Flux.of( 1, “23”, 4.0 ) or Flux.of ( listOf(1, 2, 3), listOf(“2”, “3”, “4”) )

Flux.of ( listOf(1, 2, 3), listOf(2, 3, 4) ) ok

map, flatmap, get, equals

class Flux_fox<T>(
private val data : ?list? map? array?
) {
fun <N> map(mapper): Flux_fox<N> {
for each data -> mapper apply
}
}

//kotest 작성해 오는 걸 목표로 함
```

