package ru.loginov.serbian.bot.telegram.callback

import java.util.concurrent.CancellationException

typealias TelegramCallback = suspend (CallbackData?) -> Unit

interface TelegramCallbackManager {

    fun addCallback(chatId: Long, userId: Long, block: TelegramCallback): Boolean
    fun addCallback(chatId: Long, block: TelegramCallback): Boolean

    @Throws(CancellationException::class)
    suspend fun waitCallback(chatId: Long, userId: Long?): CallbackData

}