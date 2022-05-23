package ru.loginov.serbian.bot.telegram.command.context

interface BotCommandExecuteContextFactory {

    fun createContext(userId: Long, chatId: Long, lang: String?, argumentsStr: String): BotCommandExecuteContext
    fun createEmptyContext(lang: String?): BotCommandExecuteContext

}