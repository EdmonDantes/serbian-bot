package ru.loginov.serbian.bot.telegram.callback.impl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.callback.CallbackData
import ru.loginov.serbian.bot.telegram.callback.CallbackExecutor
import ru.loginov.serbian.bot.telegram.callback.TelegramCallback
import ru.loginov.serbian.bot.telegram.callback.TelegramCallbackManager
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupButtonBuilder
import java.util.Queue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Component
class DefaultTelegramCallbackManager(
        @Qualifier("small_tasks") private val executor: Executor,
        @Qualifier("scheduler") private val scheduler: ScheduledExecutorService,
        private val dispatcher: CoroutineDispatcher,
        private val scope: CoroutineScope
) : TelegramCallbackManager, CallbackExecutor {

    private val chatCallbacks = ConcurrentHashMap<Long, Queue<TelegramCallbackWaiter>>()
    private val chatAndUserCallback = ConcurrentHashMap<Pair<Long, Long>, Queue<TelegramCallbackWaiter>>()

    override fun addCallback(
            chatId: Long,
            userId: Long?,
            timeout: Long?,
            unit: TimeUnit?,
            block: TelegramCallback
    ): Boolean {
        val queue = if (userId == null) {
            chatCallbacks.computeIfAbsent(chatId) { ConcurrentLinkedQueue() }
        } else {
            chatAndUserCallback.computeIfAbsent(chatId to userId) { ConcurrentLinkedQueue() }
        }

        val waiter = TelegramCallbackWaiter(block)
        queue.add(waiter)

        if (timeout != null && timeout > 0) {
            scheduler.schedule({ scope.launch { waiter.cancel() } }, timeout, unit ?: TimeUnit.MILLISECONDS)
        }

        return true
    }

    override suspend fun waitCallback(chatId: Long, userId: Long?, timeout: Long?, unit: TimeUnit?): CallbackData =
            suspendCoroutine { continuation ->
                addCallback(chatId, userId, timeout, unit) { data ->
                    if (data == null) {
                        continuation.resumeWithException(CancellationException("Callback is canceled", null))
                    } else {
                        continuation.resumeWith(Result.success(data))
                    }
                    true
                }
            }

    override fun invoke(chatId: Long, userId: Long?, data: CallbackData): Boolean {
        if (InlineKeyboardMarkupButtonBuilder.CANCEL_CALLBACK == data.dataFromCallback) {
            cancel(chatId, userId)
            return false
        }

        val callbacks = getCallbacks(chatId, userId) ?: return false

        if (callbacks.isEmpty()) {
            return false
        }

        val iterator = callbacks.iterator()

        while (iterator.hasNext()) {
            val callback = iterator.next()
            val shouldRemove = try {
                callback.execute(data)
            } catch (e: Exception) {
                LOGGER.warn("Failed on execute callback for chat id '$chatId' and user id '$userId'", e)
                true
            }

            if (shouldRemove) {
                iterator.remove()
            }
        }

        return true
    }

    override fun cancel(chatId: Long, userId: Long?) {
        val callbacks = getCallbacks(chatId, userId) ?: return

        while (callbacks.isNotEmpty()) {
            val callback = callbacks.poll()
            executor.execute {
                callback.execute(null)
            }
        }
    }

    private inline fun getCallbacks(chatId: Long, userId: Long?): Queue<TelegramCallbackWaiter>? = if (userId == null) {
        chatCallbacks.remove(chatId)
    } else {
        chatAndUserCallback.remove(chatId to userId)
    }

    private class TelegramCallbackWaiter(
            private val callback: TelegramCallback
    ) : TelegramCallback {
        private val executed = AtomicBoolean(false)
        private val canceled = AtomicBoolean(false)

        override fun execute(data: CallbackData?): Boolean {
            executed.set(true)
            if (!canceled.get()) {
                return callback.execute(data)
            }
            return true
        }

        fun cancel() {
            canceled.set(true)
            if (!executed.getAndSet(true)) {
                callback.execute(null)
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultTelegramCallbackManager::class.java)
    }

}