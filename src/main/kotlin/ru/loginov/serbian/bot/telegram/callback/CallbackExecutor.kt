package ru.loginov.serbian.bot.telegram.callback

interface CallbackExecutor {

    suspend fun invoke(chatId: Long, userId: Long?, data: String?): Boolean
    suspend fun cancel(chatId: Long, userId: Long?)

}