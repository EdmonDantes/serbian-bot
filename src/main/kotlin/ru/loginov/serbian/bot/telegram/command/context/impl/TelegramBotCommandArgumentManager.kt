package ru.loginov.serbian.bot.telegram.command.context.impl

import ru.loginov.serbian.bot.telegram.command.context.BotCommandArgumentManager
import ru.loginov.telegram.api.TelegramAPI

class TelegramBotCommandArgumentManager(
        private val telegram: TelegramAPI,
        private val chatId: Long
) : BotCommandArgumentManager {

    override suspend fun getNextArgument(): String {
        return ""
    }

    override suspend fun getNextArgument(name: String, description: String?): String {
        TODO("Not yet implemented")
    }

    override suspend fun getNextArgument(variants: List<String>, description: String?): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getNextArgument(variants: Map<String, String>, description: String?): String? {
        TODO("Not yet implemented")
    }
}