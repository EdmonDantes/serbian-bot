package ru.loginov.serbian.bot.telegram.callback

@FunctionalInterface
fun interface TelegramCallback {

    fun execute(data: CallbackData?): Boolean

}