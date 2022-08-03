package ru.loginov.serbian.bot.configuration

import io.github.edmondantes.simple.kotlin.callbacks.key.KeyCallbackExecutor
import io.github.edmondantes.simple.kotlin.callbacks.key.KeyCallbackStore
import io.github.edmondantes.simple.kotlin.callbacks.key.impl.DefaultKeyCallbackManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.loginov.serbian.bot.telegram.callback.CallbackData
import java.util.concurrent.Executor
import java.util.concurrent.ScheduledExecutorService

typealias TelegramCallbackStore = KeyCallbackStore<Pair<Long, Long?>, CallbackData>
typealias TelegramCallbackExecutor = KeyCallbackExecutor<Pair<Long, Long?>, CallbackData>

@Configuration
@Import(ExecutorsConfiguration::class)
class CallbacksConfiguration {

    @Bean
    @Qualifier("telegram_callback_manager")
    fun defaultKeyCallbackManager(
            @Qualifier("small_tasks") executor: Executor,
            @Qualifier("scheduler") scheduler: ScheduledExecutorService
    ): DefaultKeyCallbackManager<Pair<Long, Long?>, CallbackData> {
        return DefaultKeyCallbackManager(scheduler, executor)
    }

    @Bean
    @Qualifier("telegram_callback_store")
    fun telegramCallbackStore(
            @Qualifier("small_tasks") executor: Executor,
            @Qualifier("scheduler") scheduler: ScheduledExecutorService
    ): TelegramCallbackStore =
            defaultKeyCallbackManager(executor, scheduler)

    @Bean
    @Qualifier("telegram_callback_executor")
    fun telegramCallbackExecutor(
            @Qualifier("small_tasks") executor: Executor,
            @Qualifier("scheduler") scheduler: ScheduledExecutorService
    ) : TelegramCallbackExecutor =
            defaultKeyCallbackManager(executor, scheduler)

}