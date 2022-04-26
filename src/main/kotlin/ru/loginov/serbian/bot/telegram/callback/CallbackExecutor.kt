package ru.loginov.serbian.bot.telegram.callback

interface CallbackExecutor {

    fun invoke(chatId: Long, userId: Long?, data: String?): Boolean
    fun cancel(chatId: Long, userId: Long?)

}