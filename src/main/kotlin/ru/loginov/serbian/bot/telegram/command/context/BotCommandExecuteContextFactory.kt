package ru.loginov.serbian.bot.telegram.command.context

import ru.loginov.telegram.api.entity.Message

interface BotCommandExecuteContextFactory {

    fun createContext(
            userId: Long,
            chatId: Long,
            incomeMessage: Message,
            argumentsStr: String,
            lang: String? = null
    ): BotCommandExecuteContext

    fun createEmptyContext(lang: String?): BotCommandExecuteContext

}