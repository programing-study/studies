# 클로저 1장 - 1주

## 단순값..

43 정수
1/2 분수
"32" 문자열
:keyword 키워드, map 에서 get 함수를 대신한다.
\j 문자
true/false 진위값
nil 널, 값 없음이며, 리스트의 끝을 표현하기도 한다. 매우 중요한 의미를 갖는다.

## 컬렉션

- 불변이다.
- 존속적이다.
  리스트
- 어퍼스트로피(')로 시작하며 괄호를 이용하여 함수 호출과 구분한다. (3, 4, 5)
- 여러 자료형의 타입을 한꺼번에 내포할 수 있다.
- 쉼표가 있어도 되고 없어도 되는
  벡터
- 대괄호로 선언한다. [3 4 5]
- 여러 자료형의 타입을 한꺼번에 내포할 수 있다.
- 리스트와 다르게 인덱스로 접근 가능하다. 이 때 nth 함수를 사용한다.
  맵
- 중괄호로 선언한다. {1 3 4 5} -> {1 2, 3 4}
- 짝수개의 요소를 선언해야 하며, 홀수번째는 키, 짝수번째는 값이 된다.
- 키값들이 여러개가 될 수 있다. 키가 리스트도 될 수 있다.
- 키워드 자료형이 키가 될 경우 get 함수를 대신하여 사용될 수 있다. (:key {:key value})
  집합
- #{ }을 이용해 선언한다.
- 값이 중복되지 않는다. 중복된 값을 넣어 선언하면 에러가 발생한다.
- 키워드가 값인 경우, 키워드가 함수가 되어 집합에서 값을 리턴하는데 사용할 수 있다. (:value #{:value :value1}) -> :value
- 집합 그 자체가 함수로 작용하여 값을 반환할 수 있다. (#{:value :value1} :value1) -> :value1

## 특징

연산자로 생각되던 것들이 함수호출식으로 직접 입력된다.
/ 4.0 2
괄호가 전체를 휘감아서 함수가 호출된다 -> 이는 리스트라는 것이 괄호로 시작한다는 점에서 매우 의미심장한 표현이다.
콤마(,)가 값 구분을 위해 별도로 필요가 없다. 공백( )문자가 이를 대신한다
이름공간이 존재한다. 이는 모듈과 유사하다.
클로저는 List Processing 언어인 LISP 에서 유래한다. 따라서 리스트는 핵심 데이터 구조이다.
식의 첫 요소는 연산자 혹은 함수로 인지된다. 이 후 값들은 모두 인수, 피연산자로 해석된다.

## 등장함수

+, 이항함수 받는 함수 타입은 number 로 한정된 타입이 있을까? -> java.lang.Number cast exception (+ 3 "3")

-, 이항함수

first, 단항함수, 뒤에 컬렉션을 받아 1번 요소를 반환한다. 하스켈의 head 와 같다.

rest, 단항함수, 뒤에 컬랙션을 받아 1번 요소를 제외한 컬렉션을 반환한다. 하스켈의 tail 와 같다.

cons, 이항함수, 값 1개와 컬렉션을 받아 컬렉션에 요소가 후첨(append)된 컬렉션을 반환한다.

list, 다항함수, 여러 값을 받아 리스트를 반환한다.

nth, 이항함수, 벡터와 인덱스용 정수를 받아 벡터에 인덱스에 해당하는 요소를 반환한다.

last, 단항함수, 컬렉션을 받아 마지막 요소를 반환한다. 성능은 벡터가 전달되었을 때 더 좋다.

count, 단항함수, 컬렉션을 받아 크기를 반환한다. nil 이 전달되면 0이 리턴된다.

conj, 다항함수, 첫번째로 컬렉션을 받고, 이후 여러개의 요소값을 받아 요소들을 벡터는 후첨(append)한 컬렉션을, 리스트는 앞첨(prepend)한 컬렉션을 반환한다.

--- 절취선 ---

get, 이항함수, 맵을 받고 키를 받아 키에 대응하는 값을 가져온다. / 다항함수(3항함수)가 되는 경우도 있는데 키에 해당하는 값이 없는 경우 리턴할 기본값이 3번째 인자로 들어간다.

keys, 단항함수, 맵을 받아 키값들의 리스트를 반환한다.

vals, 단항함수, 맵을 받아 값들의 리스트를 반환한다.

assoc, 다항함수, 맵과 키, 값을 인자로 받아 맵에 있는 키에 해당하는 값을 값 인자의 값으로 하는 새 맵을 반환한다. 키가 새로운 키면 키-값이 추가된 맵이 반환된다.

dissoc, 이항함수, 맵과 키를 인자로 받아 맵에서 키를 제외한 맵을 반환한다.

merge, 다항함수, 여러 맵들을 받아 모두 합친 맵을 반환한다. 키가 같은게 나타나면 나중에 받은 인자의 값으로 덮어씌워진다.

--- 절취선 ---

clojure.set/union, 이항함수, 두 개의 집합을 받아 합집합을 반환한다.

clojure.set/difference, 이항함수, 두 개의 집합을 받아 차집합을 반환한다.

clojure.set/intersection, 이항함수, 두 개의 집합을 받아 교집합을 반환한다.

set, 단항함수, 컬렉션을 받아 집합으로 반환한다.

get, 이항함수, 집합을 받고 값을 받아 값이 있는 경우 반환한다. 없으면 nil 이 반환된다.

contains?, 이항함수,집합과 값을 인자로 받아 값의 포함여부에 대한 진위값을 반환한다. 집합을 함수로 사용하는 경우 내부 요소에 nil 이 있는 경우 nil 값을 찾지 못할 때 이 함수를 쓸 수 있다.

conj, 이항함수, 집합과 값을 받아 값이 추가된 새 집합을 반환한다.

disj, 이항함수, 집합과 값을 받아 값이 제거된 새 집합을 반환한다.

--- 논외 ---

str, 다항함수, 문자열 및 컬렉션을 받아 문자열을 반환한다..? p45 에 나오지만 설명없이 등장한 함수이다.

Use,, require :refer :all 을 조합한 단축형..

--- 절취선 종료 ---

## 기능적 문법

def, 심볼에 이름을 지정한다. 심볼이란 값이다. 즉 1급 객체들을 뜻한다. 전역적 지정이 이뤄진다.

- (def developer "superman"), 변수 선언과 유사하다.

let, 심볼에 이름을 지정하나 let 구문이 평가되는 영역 안에서만 이름이 유효하다. 영역을 벗어나면 이름값은 다시 참조되지 않는다. def 로 저장한 값은 이곳에서 일시적으로 가려진다.

- (let [developer "alice in wonderland"] developer)
- let 에서 동시에 여러 이름들을 지정할 수 있는데 이는 벡터를 통해 지정된다.

defn, 함수를 정의한다. 이름은 함수이름이 되며, 함수 파라미터는 벡터로 지정한다. 빈 벡터([])를 넣으면 무항함수가 된다.

- (defn function1 [] "hello world") OR (defn function2 [name] (+ 23 name)) -> (function2 3) => 26
- 함수 호출은 함수 이름을 ()로 감싸 호출한다.
- 단순이 이름만 넣으면 값을 리턴하게 된다.
- 하스켈과 다르게 무항함수에 인자를 넣으면 무시되지 않고 에러가 발생한다.
- 무명함수는 # 으로 지정한다. (#(str "Off we go" "!"))

ns, 이름공간을 생성한다.

- (ns my.namespace)

*ns*, 현재 이름공간을 확인할 수 있다.

- **는 귀마개라 부른다. 다시 바인딩 할 수 있는 것을 표시하는 의미라고 한다.

require, 이름공간을 import 한다.

- (require 'clojure.set')
- 여기서 ' 를 붙이는 이유는 모르겠다.
- 별칭을 지정할 수도 있다. javascript의 별칭과 유사하다. (require '[name.space :as nss')) 이후 nss.variablename 으로 참조한다.
- 이때 벡터를 사용하되 :as 를 사이에 집어넣는다. 특수한 문법인지 벡터 처리용 문법인지 이것도 무언가 있을 것 같다(저 :as를 옵션이라고 부르는 것 같다).
- require 는 직접 사용되기도 하나 ns 구문 영역 안에서 키워드 형태(:require)로 더 자주 사용된다고 한다. (ns wonderland (:require [alice.favfoods :as af]))
  //왜 리스트가..?
- :refer, :all 옵션을 이용할 수 있다고 한다. 이 경우 이름공간의 모든 심볼이 로딩되고 현재 이름공간에서 심볼 이름만으로 사용 가능하다. java 의 정적메서드의 static import 같다.
- (require [alice.favfoods :refer :all])

## 추가 코멘트

타입 추론에 관한 부분 (p.28) - common-fav-foods 함수
홑따옴표(‘)가 동작하는 방식? ex) ‘[develop “Alice in Wonderland] 하면 develop 이 정의되지 않은 값임에도 벡터가 정의된다. - 김개발 조사

## 다음주

2장 일괄
함수 퀴즈
1장 자료조사 한거 공유(김개발)

2022년 10월 12일 20시 08분 ~ 21시 05분 22초
