package ru.loginov.serbian.bot.telegram.command.context.arguments.impl

import org.slf4j.LoggerFactory
import ru.loginov.serbian.bot.telegram.callback.TelegramCallbackManager
import ru.loginov.serbian.bot.telegram.command.context.arguments.BotCommandArgumentManager
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.Message
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupButtonBuilder.Companion.CANCEL_CALLBACK
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupButtonBuilder.Companion.CONTINUE_CALLBACK
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupLineBuilder
import kotlin.coroutines.cancellation.CancellationException

class TelegramBotCommandArgumentManager(
        private val parent: BotCommandArgumentManager?,
        private val callbackManager: TelegramCallbackManager,
        private val telegram: TelegramAPI,
        private val _chatId: Long,
        private val _userId: Long?
) : BotCommandArgumentManager {

    override suspend fun getNextArgument(message: String?, optional: Boolean): String? {
        val telegramMessage = telegram.sendMessage {
            chatId = _chatId
            buildText {
                append(message ?: "Please write argument")
                buildInlineKeyboard {
                    line {
                        addUIButton(optional)
                    }
                }
            }
        }

        return waitAndRemove(telegramMessage) ?: parent?.getNextArgument(message, optional)
    }

    override suspend fun getNextArgument(variants: List<String>, message: String?, optional: Boolean): String? {
        if (variants.isEmpty()) {
            return null
        }

        val telegramMessage = telegram.sendMessage {
            chatId = _chatId
            buildText {
                append(message ?: "Please choose argument")
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
                        addUIButton(optional)
                    }
                }
            }
        }

        return waitAndRemove(telegramMessage)?.let { data ->
            data.toLongOrNull()?.let { if (variants.indices.contains(it)) variants[it.toInt()] else null }
                    ?: variants.filter { it.lowercase() == data.lowercase() }.firstOrNull()
        }
                ?: parent?.getNextArgument(variants, message, optional)
    }

    override suspend fun getNextArgument(variants: Map<String, String>, message: String?, optional: Boolean): String? {
        if (variants.isEmpty()) {
            return null
        }

        val list = variants.toList()
        val telegramMessage = telegram.sendMessage {
            chatId = _chatId
            buildText {
                append(message ?: "Please choose argument")
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
                        addUIButton(optional)
                    }
                }
            }
        }

        return waitAndRemove(telegramMessage)?.let { data ->
            data.toLongOrNull()?.let { if (list.indices.contains(it)) list[it.toInt()].second else null }
                    ?: list.filter { it.first.lowercase() == data.lowercase() }.firstOrNull()?.second
        }
                ?: parent?.getNextArgument(variants, message, optional)
    }

    private fun InlineKeyboardMarkupLineBuilder.addUIButton(optional: Boolean) {
        add {
            text = "\u274C"
            cancelCallback(_chatId, _userId)
        }
        if (optional) {
            add {
                text = "\u27A1"
                continueCallback(_chatId, _userId)
            }
        }
    }

    private suspend fun waitAndRemove(message: Message?): String? {
        try {
            val data = callbackManager.waitCallback(_chatId, _userId)
            return when (data.dataFromCallback) {
                CANCEL_CALLBACK -> {
                    throw CancellationException()
                }
                CONTINUE_CALLBACK -> {
                    null
                }
                else -> {
                    data.dataFromMessage ?: data.dataFromCallback
                }
            }
        } catch (e: Exception) {
            throw e
        } finally {
            //FIXME: Should make an architecture for bot UI
//            if (message != null) {
//                try {
//                    telegram.deleteMessage {
//                        fromMessage(message)
//                    }
//                } catch (e: Exception) {
//                    LOGGER.error("Can not delete message: '$message'", e)
//                }
//            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TelegramBotCommandArgumentManager::class.java)
    }
}