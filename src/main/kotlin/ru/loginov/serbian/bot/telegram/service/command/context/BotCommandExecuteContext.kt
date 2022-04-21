package ru.loginov.serbian.bot.telegram.service.command.context

import ru.loginov.serbian.bot.telegram.service.TelegramService

interface BotCommandExecuteContext {

    val telegramService: TelegramService
    val userId: Long
    val chatId: Long
    val lang: String?
    val arguments: List<String>

}