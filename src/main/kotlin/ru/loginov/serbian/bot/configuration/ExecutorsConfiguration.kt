package ru.loginov.serbian.bot.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
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
    fun executorForSmallTasks() : Executor {
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

    @PreDestroy
    fun preDestroy() {
        executorForSmallTasks.shutdown()
        if (!executorForSmallTasks.awaitTermination(5, TimeUnit.SECONDS)) {
            executorForSmallTasks.shutdownNow()
        }
    }

}