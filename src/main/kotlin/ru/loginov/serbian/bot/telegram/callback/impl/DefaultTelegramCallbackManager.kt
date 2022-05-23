package ru.loginov.serbian.bot.telegram.callback.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.callback.CallbackData
import ru.loginov.serbian.bot.telegram.callback.CallbackExecutor
import ru.loginov.serbian.bot.telegram.callback.TelegramCallback
import ru.loginov.serbian.bot.telegram.callback.TelegramCallbackManager
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupButtonBuilder
import java.util.Queue
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Component
class DefaultTelegramCallbackManager(
        @Qualifier("small_tasks") private val executor: Executor,
        private val coroutineScope: CoroutineScope
) : TelegramCallbackManager, CallbackExecutor {

    private val chatCallbacks = ConcurrentHashMap<Long, Queue<TelegramCallback>>()
    private val chatAndUserCallback = ConcurrentHashMap<Pair<Long, Long>, Queue<TelegramCallback>>()

    override fun addCallback(chatId: Long, userId: Long, block: TelegramCallback): Boolean {
        chatAndUserCallback.computeIfAbsent(chatId to userId) { ConcurrentLinkedQueue() }.add(block)
        return true
    }

    override fun addCallback(chatId: Long, block: TelegramCallback): Boolean {
        chatCallbacks.computeIfAbsent(chatId) { ConcurrentLinkedQueue() }.add(block)
        return true
    }

    override suspend fun waitCallback(chatId: Long, userId: Long?): CallbackData =
            suspendCoroutine { continuation ->
                if (userId == null) {
                    addCallback(chatId) { data ->
                        if (data == null) {
                            continuation.resumeWithException(CancellationException())
                        } else {
                            continuation.resumeWith(Result.success(data))
                        }
                    }
                } else {
                    addCallback(chatId, userId) { data ->
                        if (data == null) {
                            continuation.resumeWithException(CancellationException())
                        } else {
                            continuation.resumeWith(Result.success(data))
                        }
                    }
                }
            }

    override suspend fun invoke(chatId: Long, userId: Long?, data: CallbackData): Boolean {

        if (InlineKeyboardMarkupButtonBuilder.CANCEL_CALLBACK == data.dataFromCallback) {
            cancel(chatId, userId)
            return false
        }

        val callbacks = getCallbacks(chatId, userId) ?: return false

        if (callbacks.isEmpty()) {
            return false
        }
        coroutineScope {
            val futures = ArrayList<Deferred<*>>()

            while (callbacks.isNotEmpty()) {
                val callback = callbacks.poll()
                futures.add(async(coroutineScope.coroutineContext) { callback.invoke(data) })
            }

            futures.forEach {
                try {
                    it.await()
                } catch (e: Exception) {
                    LOGGER.warn("Failed on execute callback for class ", e)
                }
            }
        }

        return true
    }

    override suspend fun cancel(chatId: Long, userId: Long?) {
        val callbacks = getCallbacks(chatId, userId) ?: return

        while (callbacks.isNotEmpty()) {
            val callback = callbacks.poll()
            callback.invoke(null)
        }
    }

    private inline fun getCallbacks(chatId: Long, userId: Long?): Queue<TelegramCallback>? = if (userId == null) {
        chatCallbacks.remove(chatId)
    } else {
        chatAndUserCallback.remove(chatId to userId)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultTelegramCallbackManager::class.java)
    }

}