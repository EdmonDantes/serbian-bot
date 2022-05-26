package ru.loginov.serbian.bot.configuration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.slf4j.LoggerFactory
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
        val logger = LoggerFactory.getLogger("ExecutorForSmallTasks")
        val result = ForkJoinPool(
                Runtime.getRuntime().availableProcessors() * 2,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                { thread, exception ->
                    logger.warn(
                            "Executor on thread '${thread.name}' with id '${thread.id}' throw an exception",
                            exception
                    )
                },
                true // TODO: Need to investigate, should we use it or not?
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
    fun dispatcher(): CoroutineDispatcher = executorForSmallTasks().asCoroutineDispatcher()

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