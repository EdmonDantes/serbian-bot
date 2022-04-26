package ru.loginov.serbian.bot.telegram.callback

import java.util.concurrent.CancellationException
import kotlin.jvm.Throws

typealias TelegramCallback = (String?, Boolean) -> Unit

interface TelegramCallbackManager {

    fun addCallback(chatId: Long, userId: Long, block: TelegramCallback): Boolean
    fun addCallback(chatId: Long, block: TelegramCallback): Boolean

    @Throws(CancellationException::class)
    suspend fun waitCallback(chatId: Long, userId: Long?) : String?

}