package ru.loginov.serbian.bot.configuration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy

@Configuration
class ExecutorsConfiguration {

    private lateinit var executorForSmallTasks: ExecutorService

    @Bean
    @Qualifier("small_tasks")
    fun executorForSmallTasks(): Executor {
        executorForSmallTasks = ForkJoinPool(
                Runtime.getRuntime().availableProcessors() * 2,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                { thread, exception ->
                    exception.printStackTrace()
                },
                false
        )
        return executorForSmallTasks
    }

    @Bean
    fun coroutineScope(): CoroutineScope = CoroutineScope(executorForSmallTasks().asCoroutineDispatcher())

    @PreDestroy
    fun preDestroy() {
        executorForSmallTasks.shutdown()
        if (!executorForSmallTasks.awaitTermination(5, TimeUnit.SECONDS)) {
            executorForSmallTasks.shutdownNow()
        }
    }

}