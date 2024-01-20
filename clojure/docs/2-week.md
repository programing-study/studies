# 클로저 2장 - 2주

p.33
함수의 결과가 불린인 경우 클로저에서는 함수 이름 맨 끝에 물음표를 붙이는 것이 관례이다.

```
(true? true)
```

=> true

```
(defn foxrain? [v] (true? v))
```

특이하게 함수명에 ? 붙일 수 있다.

p. 37
seq 함수는 컬렉션을 시퀀스로 바꿔준다. 시퀀스 추상은 컬렉션을 리스트처럼 순차적으로 다룰 수 있게 해준다.

```
(seq [1 2 3])
=> (1 2 3)

```

p. 38
every?
이 함수는 인수로 진위 함수(predicate)와 컬렉션을 받는다.

```
(every? odd? [1 3 5])
=> true

```

p. 39

not-any? 함수는 진위 함수와 컬렉션을 받고, 만약 컬렉션의 요소가 하나라도 참이면 false를 반환한다.

some은 진위 함수가 평가한 값이 처음으로 논리적으로 참일 때 그 평가한 값을 반환하고 아니면 nil을 반환한다.

```
(some #(> % 3) [1 2 3 4 5])
=> true

```

p. 41
if-let은 식을 평가한 결과를 심볼에 바인딩한 후 만약 그 결과가 논리적 참이면 첫 번째 인수를 평가하고, 아니면 마지막 인수를 평가한다.

p. 42
when-let도 if-let과 비슷하다. 논리 검사 결과를 심볼에 바인딩하고, 그 결과가 참이면 본문의 식을 평가하고 아니면 nil을 반환한다.

cond 식은 검사식과 그 검사식이 참일 때 평가될 식의 쌍들을 받는다. 이것은 다른 언어의 if/else if식과 비슷하다.
디폴트 절을 추가하고 싶으면 맨 마지막 검사식 자리에 :else 키워드를 넣는다.

p. 45

그 함수에 인수 전체가 아니라 일부만이라도 적용하고 싶다. 그 후 나머지 인수를 얻게 되었을 때 마저 적용하면 된다. 이 경우 partial 함수를 사용하면 된다.

p. 46
comp는 여러 함수들을 합성해서 하나의 새로운 함수를 만든다.
합성 함수는 인수로 받은 함수들을 오른쪽부터 왼쪽으로 실행한다.

구조 분해?

### kotlin

https://kotlinlang.org/docs/destructuring-declarations.html

:or을 쓰면 맵에 해당 요소가 없을 때를 위한 디폴트 값을 설정할 수 있다.

```
(let [{flower1 :flower1 flower2 :flower2} {:flower1 "read" :flower2 "blue" :flower3 “yellow”}]
(str "The flowsers are " flower1 " and " flower2))

"The flowsers are read and blue"

(let [{:keys [flower1 flower2]}
#_=>      {:flower1 "red" :flower2 "blue"}]
#_=>  (str "The flowers are " flower1 " and " flower2))

```

p.55

```
user=> (def adjs ["normal"
#_=> "too small"
#_=> "too big"
#_=> "is swimming"])
#'user/adjs
user=> (defn alice-is [in out]
#_=>  (if (empty? in) ;
#_=>  out ;
#_=>   (alice-is ;
#_=>     (rest in) ;
#_=>     (conj out ;
#_=>         (str "Alice is " (first in))))))

```

### clojure collection

https://johngrib.github.io/wiki/clojure/study/collection/

p.56

```
user=> (defn alice-is [input]
#_=>  (loop [in input ;
#_=>         out []]
#_=>   (if (empty? in)
#_=>     out
#_=>     (recur (rest in) ;
#_=>            (conj out (str "Alice is " (first in)))))))
#'user/alice-is
user=> (alice-is adjs)
["Alice is normal" "Alice is too small" "Alice is too big" "Alice is is swimming"]

```

```
user=> (defn countdown[n]
#_=>  (if (= n 0)
#_=>   n
#_=>   (countdown (- n 1))))
#'user/countdown
user=> (countdown 3)
0
user=> (countdown 100000)
Execution error (StackOverflowError) at user/countdown (REPL:2).
null

```

### recur 적용

```
user=> (defn countdown [n]
#_=>   (if (= n 0)
#_=>    n
#_=>    (recur (- n 1))))
#'user/countdown
user=> (countdown 100000)
0
```

recur
https://johngrib.github.io/wiki/clojure/reference/special-forms/#recur-expr

추가
http://philoskim.github.io/doc/recursions.html

—
식 : 평가될 수 있는 코드
형식 : 평가될 수 있는 적법한 식
nil 은 거짓이다.
컬렉션의 비교는 각 인자를 하나씩 비교한다.
컬렉션이 비어 있지 않은 것을 검사할 때는 seq를 사용하는 것이 관용적이다. (seq []) => nil,
some 함수는 집합을 진위 함수로 이용할 수 있다.

—

### 제어구조

if 진위값 ‘참일 때 리턴값’ ‘거짓일 때 리턴값'

if-let 식을 평가한 결과를 심볼에 바인딩한 후 만약 그 결과가 논리적 참이면 첫 번째 인수를 평가하고, 아니면 마지막 인수를 평가한다.

(if-let [need-to-grow-small (> 5 1)] “drink bottle” “don’t drink bottle”) => “drink bottle”
when : 진위 함수의 결과가 참이면 본문을 평가하고 아니면 nil 을 반환한다. 검사가 참일 때만 처리하고 거짓일 때는 처리하지 않고자 할 때 사용한다.

when-let 또한 존재한다.

(when-let [need-to-grow-small false] “drink bottle”) => nil

cond : 조건식이 여러개일 때 사용한다. if/else if와 유사하다. 어떤 검사식도 참이 아니면 nil 을 반환한다. 디폴트절은 키워드를 집어넣는다.

(let [bottle “drinkme”] ( cond (= bottle “posion”) “don’t touch” (= bottle “drinkme”) “grow smaller” (= bottle “empty”)
“all gone”)) => “grow smaller”

case : 검사할 심볼이 같고 값을 = 로 비교할 수 있는 경우 cond 대신 사용한다. 참이 없는 경우에 예외를 던진다.

—

### 함수

class, 단항함수, 타입을 리턴한다.

true?, 단항함수, 인자가 참인지 리턴한다.

false?, 단항함수, 인자가 거짓인지 리턴한다.

nil?, 단항함수, 인자가 nil 인지 검사한다.

not, 단항함수, 인자의 진위값을 반전하여 리턴한다.
not=, 단항함수, not 과 = 를 결합한 함수이다.

empty?, 단항함수, 컬렉션을 인자로 받아 컬렉션이 비어있는지 확인한다.

seq, 단항함수, 컬렉션을 받아 시퀀스를 반환한다.

every?, 이항함수, 함수와 컬렉션을 받아서 모든 요소가 참으로 평가되면 참을 리턴한.

odd?, 단항함수, 정수를 받아 홀수인지 반환한다.

not-any?, 이항함수, 진위 함수와 컬렉션을 받아서 한 요소라도 참이면 거짓을 리턴한다.

some, 이항함수, 진위 함수와 컬렉션을 받아서, 평가한 값이 처음으로 논리적 참일 때 평가한 값을 반환하고 아니면 nil 을 반환한다.

– 절취선 –

### 함수를 만드는 함수

partial, 커링을 하는 방법

comp, 여러 함수들을 합성해서 하나의 새로운 함수를 만드는 함수

구조분해

```
(let [[color size] [“blue” “small”]] (str “The “ color “ door is “ size)) => “The blue door is small”
```

```
(let [[color [size]] [“blue” [“very small”]]] (str “The “ color “ door is “ size)) => “The blue door is very small”

```

처음 자료구조 전체를 맵핑 :as

```
(let [[color [size] :as original] [“blue” [“small”]]] {:color color :size size :original original}) => {:color “blue”, :size “small”, :original [“blue” [“small”]]}

```

맵 구조분해, :or 는 맵에 해당 요소가 없을 때를 위한 디폴트 값, 전체 자료구조는 :as 로 동일, 심볼의 이름을 키와 같게 하므로 :keys로 아예 처리하기도 함

```
(let [{flower1 :flower1 flower2 :flower2 :or {flower2 “missing”}} {:flower1 “red”}] (str “The flowers are “ flower1 “ and “ flower2)) -> “The flower are red and missing”

```

defn으로 함수 적용시에도 구조분해를 적용 가능

### 지연

take, 이항함수, 양의 정수와 시퀀스를 받아 양의 정수개만큼 취한 시퀀스를 리턴한다

range, 다항함수, 인자를 안 주면 무한시퀀스(지연), 인자를 주면 범위의 끝(미만)을 지정할 수 있다.

repeat, 이항핫무, 양의 정수와 값을 받아 값을 양의 정수만큼 반복하는 지연 시퀀스를 반환한다.

rand-int, 단항함수, 정수를 받아 0과 정수 사이 임의 정수를 리턴한다.

repeatedly, 이항함수, 양의 정수와 함수를 받아 함수의 리턴값을 양의 정수개만큼 반복한 지연 시퀀스를 반환한다.

cycle, 단항함수, 컬렉션을 인수로 받아 무한 지연 시퀀스를 반환한다.

### 재귀

loop, 재귀를 쉽게 지원한다. 자신 안에 있는 코드를 반복해서 실행한다. 조건에 만족할 때까지 실행할 수도 무한히 실행할 수도 있다. recur 가 재귀호출부가 된다.

recur : 재귀 호출 시 스택을 소모하지 않는다. loop 가 없이 사용되면 함수 자체가 재귀점이다

```
(defn alice-is [input] (loop [in input out []] (if (empty? in) out (recur (rest in) (conj out (str “Alice is “ (first in)))))))
```

### 데이터 변환

map

reduce

‘ 의미
https://stackoverflow.com/questions/69782690/what-does-a-quote-mean-in-front-of-a-vector-in-clojure
