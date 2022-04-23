package ru.loginov.serbian.bot.telegram.command.context

interface BotCommandExecuteContextFactory {

    fun createContext(userId: Long, charId: Long, lang: String?, arguments: List<String>) : BotCommandExecuteContext

}