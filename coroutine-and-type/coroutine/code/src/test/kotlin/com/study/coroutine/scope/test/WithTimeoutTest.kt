package com.study.coroutine.scope.test

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class WithTimeoutTest {

    @DisplayName("normal test")
    @Test
    fun testTime2() = runTest {
        withTimeout(1000) {
            // 1000 ms 보다 적게 걸리는 작업
            delay(900)
        }
    }

    @DisplayName("timeout test")
    @Test
    fun testTime1() = runTest {

        assertThrows<TimeoutCancellationException> {
            withTimeout(1000) {
                // 1000ms보다 오래 걸리는 작업
                delay(1100)
            }
        }
    }

    @DisplayName("normal test 2")
    @Test
    fun testTime3() = runBlocking {
        withTimeout(1000) {
            delay(900)
        }
    }
}
