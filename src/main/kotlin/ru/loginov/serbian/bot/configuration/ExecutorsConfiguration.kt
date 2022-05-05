package ru.loginov.serbian.bot.configuration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.PreDestroy

@Configuration
class ExecutorsConfiguration {

    private var executorForSmallTasks = AtomicReference<ExecutorService>()
    private var scheduler = AtomicReference<ScheduledExecutorService>()

    @Bean
    @Qualifier("small_tasks")
    fun executorForSmallTasks(): Executor {
        val result = ForkJoinPool(
                Runtime.getRuntime().availableProcessors() * 2,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                { thread, exception ->
                    exception.printStackTrace()
                },
                false
        )
        executorForSmallTasks.set(result);
        return result
    }

    @Bean
    @Qualifier("scheduler")
    fun scheduler() : ScheduledExecutorService {
        val result = ScheduledThreadPoolExecutor(1)
        scheduler.set(result)
        return result
    }

    @Bean
    fun coroutineScope(): CoroutineScope = CoroutineScope(executorForSmallTasks().asCoroutineDispatcher())

    @PreDestroy
    fun preDestroy() {
        executorForSmallTasks.get()?.apply {
            turnDown()
        }
        scheduler.get()?.apply {
            turnDown()
        }
    }

    private inline fun ExecutorService.turnDown() {
        shutdown()
        if (!awaitTermination(5, TimeUnit.SECONDS)) {
            shutdownNow()
        }
    }

}