package ru.loginov.serbian.bot.telegram.command.context

import ru.loginov.telegram.api.TelegramAPI

interface BotCommandExecuteContext {

    val telegramApi: TelegramAPI
    val userId: Long
    val chatId: Long
    val lang: String?
    val arguments: List<String>

}