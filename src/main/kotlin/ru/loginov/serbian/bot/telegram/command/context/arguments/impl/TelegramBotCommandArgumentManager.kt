package ru.loginov.serbian.bot.telegram.command.context.arguments.impl

import ru.loginov.serbian.bot.telegram.callback.TelegramCallbackManager
import ru.loginov.serbian.bot.telegram.command.context.arguments.BotCommandArgumentManager
import ru.loginov.telegram.api.TelegramAPI

class TelegramBotCommandArgumentManager(
        private val parent: BotCommandArgumentManager?,
        private val callbackManager: TelegramCallbackManager,
        private val telegram: TelegramAPI,
        private val _chatId: Long,
        private val _userId: Long?
) : BotCommandArgumentManager {

    override suspend fun getNextArgument(): String {
        telegram.sendMessage {
            chatId = _chatId
            buildText {
                append("Please write argument:")
                buildInlineKeyboard {
                    line {
                        add {
                            text = "Cancel"
                            cancelCallback(_chatId, _userId)
                        }
                    }
                }
            }
        }

        return callbackManager.waitCallback(_chatId, _userId)
                ?: throw IllegalStateException("Can not get data from callback")
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