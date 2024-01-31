package com.study.chapter1


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
