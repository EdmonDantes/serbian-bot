package ru.loginov.serbian.bot.telegram.service.command.context.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.telegram.service.TelegramService
import ru.loginov.serbian.bot.telegram.service.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.service.command.context.BotCommandExecuteContextFactory

@Component
class DefaultBotCommandExecuteContextFactory : BotCommandExecuteContextFactory {

    @Autowired
    private lateinit var telegramService: TelegramService

    override fun createContext(userId: Long, charId: Long, lang: String?, arguments: List<String>): BotCommandExecuteContext =
            DefaultBotCommandExecuteContext(telegramService, userId, charId, lang, arguments)
}