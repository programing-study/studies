package chapter2

class InfiniteLoop

suspend fun main() {

    println(nextNum().take(10).toList())

}

suspend fun nextNum() = sequence {
    var num = 0
    while (true) {
        yield(num)
        num++
    }
}
