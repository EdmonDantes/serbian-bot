package ru.loginov.serbian.bot.telegram.command.context.arguments.impl

import org.slf4j.LoggerFactory
import ru.loginov.serbian.bot.telegram.callback.TelegramCallbackManager
import ru.loginov.serbian.bot.telegram.command.context.arguments.BotCommandArgumentManager
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.Message
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupLineBuilder

class TelegramBotCommandArgumentManager(
        private val parent: BotCommandArgumentManager?,
        private val callbackManager: TelegramCallbackManager,
        private val telegram: TelegramAPI,
        private val _chatId: Long,
        private val _userId: Long?
) : BotCommandArgumentManager {

    override suspend fun getNextArgument(): String? {
        val message = telegram.sendMessage {
            chatId = _chatId
            buildText {
                append("Please write argument:")
                buildInlineKeyboard {
                    line {
                        addCancelButton()
                    }
                }
            }
        }

        return waitAndRemove(message) ?: parent?.getNextArgument()
    }

    override suspend fun getNextArgument(name: String, description: String?): String? {
        val message = telegram.sendMessage {
            chatId = _chatId
            buildText {
                append("Please write $name${description?.let { " $description" } ?: ""}")
                buildInlineKeyboard {
                    line {
                        addCancelButton()
                    }
                }
            }
        }

        return waitAndRemove(message) ?: parent?.getNextArgument(name, description)
    }

    override suspend fun getNextArgument(variants: List<String>, description: String?): String? {
        if (variants.isEmpty()) {
            return null
        }

        val message = telegram.sendMessage {
            chatId = _chatId
            buildText {
                append("Please choose${description?.let { " $description" } ?: ""}")
                buildInlineKeyboard {
                    variants.forEachIndexed { index, it ->
                        line {
                            add {
                                text = it
                                callbackData(_chatId, _userId, index)
                            }
                        }
                    }
                    line {
                        addCancelButton()
                    }
                }
            }
        }

        return waitAndRemove(message)?.let { data ->
            data.toLongOrNull()?.let { if (variants.indices.contains(it)) variants[it.toInt()] else null }
                    ?: variants.filter { it.lowercase() == data.lowercase() }.firstOrNull()
        }
                ?: parent?.getNextArgument(variants, description)
    }

    override suspend fun getNextArgument(variants: Map<String, String>, description: String?): String? {
        if (variants.isEmpty()) {
            return null
        }

        val list = variants.toList()
        val message = telegram.sendMessage {
            chatId = _chatId
            buildText {
                append("Please choose${description?.let { " $description" } ?: ""}")
                buildInlineKeyboard {
                    list.forEachIndexed { index, pair ->
                        line {
                            add {
                                text = pair.first
                                callbackData(_chatId, _userId, index)
                            }
                        }
                    }
                    line {
                        addCancelButton()
                    }
                }
            }
        }

        return waitAndRemove(message)?.let { data ->
            data.toLongOrNull()?.let { if (list.indices.contains(it)) list[it.toInt()].second else null }
                    ?: list.filter { it.first.lowercase() == data.lowercase() }.firstOrNull()?.second
        }
                ?: parent?.getNextArgument(variants, description)
    }

    private fun InlineKeyboardMarkupLineBuilder.addCancelButton() {
        add {
            text = "Cancel"
            cancelCallback(_chatId, _userId)
        }
    }

    private suspend fun waitAndRemove(message: Message?) : String? {
        try {
            return callbackManager.waitCallback(_chatId, _userId)
        } catch (e: Exception) {
            throw e
        } finally {
            if (message != null) {
                try {
                    telegram.deleteMessage {
                        fromMessage(message)
                    }
                } catch (e: Exception) {
                    LOGGER.error("Can not delete message: '$message'")
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TelegramBotCommandArgumentManager::class.java)
    }
}