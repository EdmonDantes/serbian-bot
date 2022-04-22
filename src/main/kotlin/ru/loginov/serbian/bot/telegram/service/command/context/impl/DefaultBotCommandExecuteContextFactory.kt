package ru.loginov.serbian.bot.telegram.service.command.context.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.service.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.service.command.context.BotCommandExecuteContextFactory
import ru.loginov.telegram.api.TelegramAPI

@Component
class DefaultBotCommandExecuteContextFactory : BotCommandExecuteContextFactory {

    @Autowired
    private lateinit var telegramApi: TelegramAPI

    override fun createContext(
            userId: Long,
            charId: Long,
            lang: String?,
            arguments: List<String>
    ): BotCommandExecuteContext =
            DefaultBotCommandExecuteContext(telegramApi, userId, charId, lang, arguments)
}