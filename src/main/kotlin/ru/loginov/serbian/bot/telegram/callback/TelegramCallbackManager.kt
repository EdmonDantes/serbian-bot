package ru.loginov.serbian.bot.telegram.callback

import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

interface TelegramCallbackManager {

    fun addCallback(
            chatId: Long,
            userId: Long? = null,
            timeout: Long? = null,
            unit: TimeUnit = TimeUnit.MILLISECONDS,
            block: TelegramCallback
    ): Boolean

    @Throws(CancellationException::class)
    suspend fun waitCallback(
            chatId: Long,
            userId: Long?,
            timeout: Long? = null,
            unit: TimeUnit = TimeUnit.MILLISECONDS
    ): CallbackData

}