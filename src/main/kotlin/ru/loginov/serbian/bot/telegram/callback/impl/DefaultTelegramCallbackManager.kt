package ru.loginov.serbian.bot.telegram.callback.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
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
class DefaultTelegramCallbackManager : TelegramCallbackManager, CallbackExecutor {

    @Autowired
    @Qualifier("small_tasks")
    private lateinit var executor: Executor

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

    override suspend fun waitCallback(chatId: Long, userId: Long?): String? =
            suspendCoroutine { continuation ->
                if (userId == null) {
                    addCallback(chatId) { data, canceled ->
                        if (canceled) {
                            continuation.resumeWithException(CancellationException())
                        } else {
                            continuation.resumeWith(Result.success(data))
                        }
                    }
                } else {
                    addCallback(chatId, userId) { data, canceled ->
                        if (canceled) {
                            continuation.resumeWithException(CancellationException())
                        } else {
                            continuation.resumeWith(Result.success(data))
                        }
                    }
                }
            }

    override fun invoke(chatId: Long, userId: Long?, data: String?): Boolean {

        if (InlineKeyboardMarkupButtonBuilder.CANCEL_CALLBACK == data) {
            cancel(chatId, userId)
            return false
        }

        val callbacks = getCallbacks(chatId, userId) ?: return false

        if (callbacks.isEmpty()) {
            return false
        }

        while (callbacks.isNotEmpty()) {
            val callback = callbacks.poll()
            executor.execute {
                callback.invoke(data, false)
            }
        }

        return true
    }

    override fun cancel(chatId: Long, userId: Long?) {
        val callbacks = getCallbacks(chatId, userId) ?: return

        while (callbacks.isNotEmpty()) {
            val callback = callbacks.poll()
            executor.execute {
                callback.invoke(null, true)
            }
        }
    }

    private inline fun getCallbacks(chatId: Long, userId: Long?): Queue<TelegramCallback>? = if (userId == null) {
        chatCallbacks.remove(chatId)
    } else {
        chatAndUserCallback.remove(chatId to userId)
    }

}