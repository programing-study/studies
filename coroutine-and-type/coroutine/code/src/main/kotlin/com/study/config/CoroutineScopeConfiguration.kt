package com.study.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoroutineScopeConfiguration {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Bean
    fun coroutineDispatcher(): CoroutineDispatcher =
        Dispatchers.IO.limitedParallelism(5)

    @Bean
    fun coroutineExceptionHandler() = CoroutineExceptionHandler { _, throwable ->

    }

    @Bean
    fun coroutineScope(
        coroutineDispatcher: CoroutineDispatcher,
        coroutineExceptionHandler: CoroutineExceptionHandler
    ) = CoroutineScope(
        SupervisorJob() +
                coroutineDispatcher + coroutineExceptionHandler
    )
}
