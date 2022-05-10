package ru.loginov.serbian.bot.telegram.callback.impl

import org.springframework.beans.factory.annotation.Autowired
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

        while (callbacks.isNotEmpty()) {
            val callback = callbacks.poll()
            callback.invoke(data)
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

}