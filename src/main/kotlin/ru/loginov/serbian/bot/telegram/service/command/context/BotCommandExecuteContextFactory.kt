package ru.loginov.serbian.bot.telegram.service.command.context

interface BotCommandExecuteContextFactory {

    fun createContext(userId: Long, charId: Long, lang: String?, arguments: List<String>) : BotCommandExecuteContext

}