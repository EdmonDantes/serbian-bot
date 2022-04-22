package ru.loginov.serbian.bot.telegram.service.command.context.impl

import ru.loginov.serbian.bot.telegram.service.command.context.BotCommandExecuteContext
import ru.loginov.telegram.api.TelegramAPI

class DefaultBotCommandExecuteContext(
        override val telegramApi: TelegramAPI,
        override val userId: Long,
        override val chatId: Long,
        override val lang: String?,
        override val arguments: List<String>
) : BotCommandExecuteContext