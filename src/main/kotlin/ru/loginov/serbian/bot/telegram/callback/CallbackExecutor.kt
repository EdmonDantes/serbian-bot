package ru.loginov.serbian.bot.telegram.callback

interface CallbackExecutor {

    suspend fun invoke(chatId: Long, userId: Long?, data: CallbackData): Boolean
    suspend fun cancel(chatId: Long, userId: Long?)

}