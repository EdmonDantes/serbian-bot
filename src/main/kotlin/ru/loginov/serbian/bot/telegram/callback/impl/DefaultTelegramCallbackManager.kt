package ru.loginov.serbian.bot.telegram.callback.impl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.callback.CallbackData
import ru.loginov.serbian.bot.telegram.callback.CallbackExecutor
import ru.loginov.serbian.bot.telegram.callback.TelegramCallback
import ru.loginov.serbian.bot.telegram.callback.TelegramCallback.Companion.CANCEL_CALLBACK
import ru.loginov.serbian.bot.telegram.callback.TelegramCallbackManager
import java.util.concurrent.Executor
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Component
class DefaultTelegramCallbackManager(
        @Qualifier("small_tasks") private val executor: Executor,
        @Qualifier("scheduler") private val scheduler: ScheduledExecutorService,
) : TelegramCallbackManager, CallbackExecutor {

    private val nextCallbackId = AtomicInteger(1)

    private val readWriteLock = ReentrantReadWriteLock()
    private val chatCallbacks = HashMap<Long, MutableMap<Int, TelegramCallbackHandler>>()
    private val chatAndUserCallback = HashMap<Pair<Long, Long>, MutableMap<Int, TelegramCallbackHandler>>()

    override fun addCallback(
            chatId: Long,
            userId: Long?,
            timeout: Long?,
            unit: TimeUnit,
            block: TelegramCallback
    ): Boolean {
        val queue = readWriteLock.write {
            userId?.let { chatAndUserCallback.computeIfAbsent(chatId to userId) { HashMap() } }
                    ?: chatCallbacks.computeIfAbsent(chatId) { HashMap() }
        }

        val callbackId = nextCallbackId.getAndIncrement()
        val handler = TelegramCallbackHandler(block)
        synchronized(queue) {
            queue[callbackId] = handler
        }

        if (timeout != null && timeout > 0) {
            scheduler.schedule({ cancelCallback(chatId, userId, callbackId) }, timeout, unit)
        }

        return true
    }

    override suspend fun waitCallback(chatId: Long, userId: Long?, timeout: Long?, unit: TimeUnit): CallbackData =
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
        if (CANCEL_CALLBACK == data.dataFromCallback) {
            cancel(chatId, userId)
            return true
        }

        val callbacks = getCallbacks(chatId, userId) ?: return false

        synchronized(callbacks) {
            if (callbacks.isEmpty()) {
                return false
            }

            val iterator = callbacks.iterator()

            while (iterator.hasNext()) {
                val (_, callback) = iterator.next()

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
        }

        return true
    }

    override fun cancel(chatId: Long, userId: Long?) {
        val callbacks = getCallbacks(chatId, userId) ?: return

        synchronized(callbacks) {
            callbacks.forEach { (callbackId, callback) ->
                executor.execute {
                    try {
                        callback.cancel()
                    } catch (e: Exception) {
                        LOGGER.warn(
                                "Can not cancel callback for chat '$chatId', user '$userId' with id '$callbackId'",
                                e
                        )
                    }
                }
            }

            callbacks.clear()
        }
    }

    private fun cancelCallback(chatId: Long, userId: Long?, callbackId: Int) {
        val map = getCallbacks(chatId, userId) ?: return
        val callback = synchronized(map) {
            map.remove(callbackId)
        }

        try {
            callback?.cancel()
        } catch (e: Exception) {
            LOGGER.warn("Can not cancel callback for chat '$chatId', user '$userId' with id '$callbackId'", e)
        }
    }

    private fun getCallbacks(chatId: Long, userId: Long?): MutableMap<Int, TelegramCallbackHandler>? =
            readWriteLock.read {
                userId?.let { chatAndUserCallback[chatId to userId] } ?: chatCallbacks[chatId]
            }

    private class TelegramCallbackHandler(
            private val callback: TelegramCallback
    ) : TelegramCallback {
        private val canceled = AtomicBoolean(false)

        override fun execute(data: CallbackData?): Boolean {
            if (!canceled.get()) {
                return callback.execute(data)
            }
            return true
        }

        fun cancel() {
            if (!canceled.getAndSet(true)) {
                callback.execute(null)
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultTelegramCallbackManager::class.java)
    }

}