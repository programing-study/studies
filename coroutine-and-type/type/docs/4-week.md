# 오버로딩에 의한 다형성

## 오버로딩

### 함수오버로딩(function overloading)

한 함수가 여러 타입의 인자를 받아야 할 때 이거나 타입보다 훨씬 간단하고 직관적인 해결책을 제공하는 기능이다.  
함수 오버로딩이란 같은 이름의 함수를 여러개 저의하는 것이다. 단, 이때 이름이 같은 함수들의 매개변수 타입은 서로 달라야 한다는 뜻으로 나온다.

> 함수가 오버로딩 되어이 있을 때 호출할 함수를 자동으로 고르는 것을 함수 선택(function dispatch)라고 부른다.
> 함수 선택의 가장 기본적인 규칙은 `인자 타입에 맞는 함수를 고른다`는 것이다.

함수 오버로딩은 다형성을 만들어 내는 세 번째 방법이다. 같은 이름을 여러번 정의함으로써 만들어지는 다형성을 오버로딩에 의한 다형성(ad hoc polymorphism)
이라 부른다.

```
void write(Cell cell, String str)
void write(Cell cell, Int num)
```

오버로딩이란 함수 오버로딩, 메서드 오버로딩, 연산자 오버로딩을 모두 통틀어 일컫는 용어다.

| 이름       | 정의                    |
|----------|-----------------------|
| 함수 오버로딩  | 같은 이름의 함수를 여럿 정의하는 것  |
| 메서드 오버로딩 | 같은 이름의 메서드를 여럿 정의하는 것 |
| 연산자 오버로딩 | 같은 연산자를 여럿 정의하는 것     |

### 함수 오버로딩

```Kotlin

class OverloadingExample {
    // 정수형 덧셈 함수
    fun add(a: Int, b: Int): Int {
        return a + b
    }

    // 실수형 덧셈 함수
    fun add(a: Double, b: Double): Double {
        return a + b
    }
}

fun main() {
    val obj = OverloadingExample()
    println(obj.add(1, 2)) // 정수형 덧셈 함수 호출
    println(obj.add(1.5, 2.5)) // 실수형 덧셈 함수 호출
}


```

### 메서드 오버로딩

```Kotlin
class OverloadingExample {
    // 정수형 덧셈 메서드
    fun add(a: Int, b: Int) {
        println("정수형 덧셈: ${a + b}")
    }

    // 문자열 이어붙이기 메서드
    fun add(a: String, b: String) {
        println("문자열 이어붙이기: ${a + b}")
    }
}

fun main() {
    val obj = OverloadingExample()
    obj.add(1, 2) // 정수형 덧셈 메서드 호출
    obj.add("Hello, ", "World!") // 문자열 이어붙이기 메서드 호출
}

```

### 함수 오버로딩

```Kotlin
data class Vector(val x: Double, val y: Double) {
    // 벡터 덧셈 연산을 재정의
    operator fun plus(other: Vector): Vector {
        val newX = this.x + other.x
        val newY = this.y + other.y
        return Vector(newX, newY)
    }
}

fun main() {
    val v1 = Vector(1.0, 2.0)
    val v2 = Vector(3.0, 4.0)
    val sum = v1 + v2 // 벡터 덧셈 연산 호출
    println("합 벡터: (${sum.x}, ${sum.y})")
}

```

## 함수 오버로딩 사용할 때 주의사항

정적 선택을 잘 이해해야 한다. 어떤 경우 내 기대와 다른 함수가 선택되는지 알고 있어야 나도 모르는 사이에 버그를 만들어 내는 일을 막을 수 있다.

### 정적 선택이란?

정적 선택(Static Resolution)은 함수 오버로딩 시에 컴파일러가 적절한 메서드를 선택하는 과정을 의미합니다. 이것은 컴파일 시간에 이루어지며, 선택은 해당 호출 시 사용된 매개변수의 타입과 개수를
기반으로 합니다.

```Java 
public class OverloadingExample {
    // 정수형 덧셈 함수
    public void add(int a, int b) {
        System.out.println("정수형 덧셈: " + (a + b));
    }

    // 실수형 덧셈 함수
    public void add(double a, double b) {
        System.out.println("실수형 덧셈: " + (a + b));
    }

    public static void main(String[] args) {
        OverloadingExample obj = new OverloadingExample();
        int int1 = 1;
        int int2 = 2;
        double double1 = 1.5;
        double double2 = 2.5;

        // 정적 선택에 의해 int 타입 매개변수를 받는 add(int, int) 메서드 호출
        obj.add(int1, int2);
        // 정적 선택에 의해 double 타입 매개변수를 받는 add(double, double) 메서드 호출
        obj.add(double1, double2);
        // 정적 선택에 의해 정확한 매개변수 타입이 없어 오류 발생
        // obj.add(int1, double1); // 주석 해제 후 컴파일 시 오류 확인 가능
    }
}

```

위의 코드에서 add 메서드를 호출할 때, 컴파일러는 매개변수의 타입과 개수를 기반으로 적절한 메서드를 선택합니다. 예를 들어, obj.add(int1, int2)를 호출하면 add(int, int) 메서드가
선택되고, obj.add(double1, double2)를 호출하면 add(double, double) 메서드가 선택됩니다.

그러나 obj.add(int1, double1)과 같이 서로 다른 타입의 매개변수를 사용하면 컴파일러가 적절한 메서드를 선택하지 못하고 오류가 발생합니다. 이는 정적 선택이 매개변수의 타입과 개수를 정확하게 비교하여
메서드를 선택하기 때문입니다.

### 함수 선택 규칙

1. 인자 타입에 맞는 함수를 고른다.
2. (인자의 타입에 맞는 함수가 여럿이면) 인자의 타입에 가장 특화된 함수를 고른다.
3. 함수를 고를 때는 인자의 정적 타입만 고려한다.

## 메서드 오버라이딩

메서드 오버라이딩은 클래스를 상속해서 자식 클래스(child class)에 메서드를 새로 정의할 때 메서드의 이름과 매개변수 타입을 부모 클래스(parent class)에 정의되어 있는 메서드와 똑같게 정의하는 것을
말한다.

overriding이라는 단어의 사전적인 뜻은 '자동으로 진행되는 동작을 사람이 개입하여 중단시킨 뒤 스스로 조작하는 것'이다.

메서드 오버라이딩시에 언급되는 정적 타입과 동적타입

메서드 오버라이딩시에 B는 A이다와 같이 상속관계에 있다고 가정하면

A는 정적타입  
B는 동적타입

이 된다.

### 메서드 선택 규칙

1. 인자 타입에 맞는 메서드를 고른다.
2. (인자 타입에 맞는 메서드가 여럿이면) 인자 타입에 가장 특화된 메서드를 고른다.
3. 메서드를 고를 때는 인자의 정적 타입을 고려한다.
4. 메서드를 고를 때는 수신자의 동적 타입도 고려한다.

## 타입 클래스

타입클래스는 특정 타입을 위한 어떤 함수가 존재한다는 사실을 표현한다. '타입 클래스'라는 용어에 '클래스'가 포함되기는 하지만 타입클래스는 클래스와 연관이 없다.   
타입클래스는 클래스가 아니며 타입을 나타내는 클래스는 더더욱 아니다. 다만 `타입이 만족해야 하는 조건을 표현한다는 점에서 추상 클래스와 비슷한 역할`을 한다.

```
typeclass Comparable<T> {   // 타입 매개변수 T 선언
    Boolean gt(T v1, T v2); // 타입 매개변수 T 사용가능
}
```

## 타입 매개변수

제네릭 프로그래밍에서 사용되는 개념으로, 클래스나 메서드에서 사용되는 타입을 지정하는 데 사용됩니다. 타입 매개변수를 사용하면 클래스나 메서드를 여러 종류의 타입에 대해 동작할 수 있도록 만들 수 있습니다.
이를 통해 코드의 재사용성과 유연성을 향상시킬 수 있습니다.

```
instance <T> Comparable<List<T>> {          //타입 매개변수 <T> 선언
    Boolean gt(List<T> v1, List<T> v2) {
        return v1.lenth > v2.length
    }
}
```

마지막에 마치며 부분에 있는 내용

러스트는 기존 시스템 프로그래밍 분야에서 사용되던 C와 C++의 고질적인 문제들을 해결하기 위해 등장한 언어다.
C와 C++가 겪는 많은 문제의 원인은 너무나도 부실한 타입검사에 있었다..
...

러스트에 대해서 새롭게 관심이 생겨나는 지점임.

