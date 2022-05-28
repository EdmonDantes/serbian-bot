package ru.loginov.serbian.bot.configuration

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
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
import javax.annotation.PreDestroy

@Configuration
class ExecutorsConfiguration {

    private var executorForSmallTasks: ExecutorService? = null
    private var scheduler: ScheduledExecutorService? = null
    private var coroutineDispatcher: CoroutineDispatcher? = null
    private var coroutineScope: CoroutineScope? = null

    @Bean
    @Qualifier("small_tasks")
    fun executorForSmallTasks(): Executor {
        val logger = LoggerFactory.getLogger("ExecutorForSmallTasks")
        executorForSmallTasks = ForkJoinPool(
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
        return executorForSmallTasks!!;
    }

    @Bean
    @Qualifier("scheduler")
    fun scheduler(): ScheduledExecutorService {
        scheduler = ScheduledThreadPoolExecutor(1)
        return scheduler!!
    }

    @Bean
    fun coroutineDispatcher(): CoroutineDispatcher {
        coroutineDispatcher = executorForSmallTasks().asCoroutineDispatcher()
        return coroutineDispatcher!!
    }

    @Bean
    fun coroutineScope(coroutineDispatcher: CoroutineDispatcher): CoroutineScope {
        coroutineScope = CoroutineScope(coroutineDispatcher)
        return coroutineScope!!
    }

    @PreDestroy
    fun preDestroy() {
        try {
            coroutineScope?.cancel("Turn off")
        } catch (e: Exception) {
            LOGGER.error("Can not cancel and turn off coroutine scope", e)
        }

        try {
            coroutineDispatcher?.cancel(CancellationException("Turn off"))
        } catch (e: Exception) {
            LOGGER.error("Can not cancel and turn off coroutine dispatcher")
        }

        try {
            executorForSmallTasks?.turnDown()
        } catch (e: Exception) {
            LOGGER.error("Can not turn down executor for small tasks", e)
        }

        try {
            scheduler?.turnDown()
        } catch (e: Exception) {
            LOGGER.error("Can not turn down scheduler", e)
        }
    }

    private inline fun ExecutorService.turnDown() {
        shutdown()
        if (!awaitTermination(5, TimeUnit.SECONDS)) {
            shutdownNow()
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ExecutorsConfiguration::class.java)
    }

}