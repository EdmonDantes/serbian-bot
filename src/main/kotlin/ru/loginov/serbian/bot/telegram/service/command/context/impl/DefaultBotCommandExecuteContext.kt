package ru.loginov.serbian.bot.telegram.service.command.context.impl

import ru.loginov.serbian.bot.telegram.service.TelegramService
import ru.loginov.serbian.bot.telegram.service.command.context.BotCommandExecuteContext

class DefaultBotCommandExecuteContext(
        override val telegramService: TelegramService,
        override val userId: Long,
        override val chatId: Long,
        override val lang: String?,
        override val arguments: List<String>
) : BotCommandExecuteContext