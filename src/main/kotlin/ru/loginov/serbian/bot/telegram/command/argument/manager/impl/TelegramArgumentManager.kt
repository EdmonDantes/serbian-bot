package ru.loginov.serbian.bot.telegram.command.argument.manager.impl

import kotlinx.coroutines.CancellationException
import org.slf4j.LoggerFactory
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.spring.localization.context.LocalizationContext
import ru.loginov.serbian.bot.telegram.callback.TelegramCallback.Companion.CANCEL_CALLBACK
import ru.loginov.serbian.bot.telegram.callback.TelegramCallback.Companion.CONTINUE_CALLBACK
import ru.loginov.serbian.bot.telegram.callback.TelegramCallbackManager
import ru.loginov.serbian.bot.telegram.callback.callbackData
import ru.loginov.serbian.bot.telegram.callback.continueCallback
import ru.loginov.serbian.bot.telegram.command.argument.AnyArgument
import ru.loginov.serbian.bot.telegram.command.argument.configure
import ru.loginov.serbian.bot.telegram.command.argument.impl.DefaultArgument
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.value.ArgumentValue
import ru.loginov.serbian.bot.telegram.command.argument.value.impl.DefaultArgumentValue
import ru.loginov.serbian.bot.telegram.command.argument.value.transform
import ru.loginov.serbian.bot.telegram.command.argument.value.transformOrEmpty
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.Location
import ru.loginov.telegram.api.entity.Message
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupBuilder
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupLineBuilder
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TelegramArgumentManager(
        private val telegram: TelegramAPI,
        private val callbackManager: TelegramCallbackManager,
        private val localizationContext: LocalizationContext,
        private val localizationManager: LocalizationManager,
        private val _chatId: Long,
        private val _userId: Long?,
) : ArgumentManager {
    override fun choose(name: String, message: String?): AnyArgument<Boolean> =
            DefaultArgument(name) {
                val msg = telegram.sendMessage {
                    chatId = _chatId
                    markdown2 {
                        append(
                                localizationContext.transformStringToLocalized(
                                        message ?: "@{bot.abstract.command.please.choose.argument}"
                                )
                        )
                    }
                    buildInlineKeyboard {
                        line {
                            add {
                                text = "\u2705"
                                callbackData(_chatId, _userId, 1)
                            }
                            add {
                                text = "\u274C"
                                callbackData(_chatId, _userId, 2)
                            }
                        }
                        if (it.isOptional) {
                            line {
                                addContinueButton()
                            }
                        }
                    }
                }

                waitResult(msg).transform { it.toIntOrNull() == 1 }
            }

    override fun language(name: String, message: String?): AnyArgument<String> = DefaultArgument(name) { definition ->
        val menu = localizationManager.supportLanguages.mapNotNull { lang ->
            localizationManager.findLocalizedStringByKey(
                    lang,
                    "language.$lang"
            )?.let { it to lang }
        }.toMap()

        argument(name, menu, message).configure(definition).process()
    }

    override fun location(name: String, message: String?): AnyArgument<Pair<Double, Double>> = DefaultArgument(name) {
        val msg = telegram.sendMessage {
            chatId = _chatId
            markdown2 {
                append(
                        localizationContext.transformStringToLocalized(
                                message ?: "@{bot.abstract.command.please.write.argument}"
                        )
                )
                buildInlineKeyboard {
                    addUserActionButtons(it.isOptional)
                }
            }
        }

        try {
            val location = suspendCoroutine<Location> { continuation ->
                callbackManager.addCallback(_chatId, _userId, TIMEOUT_ARGUMENT_MS, TimeUnit.MILLISECONDS) { data ->
                    if (data == null) {
                        continuation.resumeWithException(CancellationException("Callback was cancelled by user"))
                        true
                    } else if (data.location != null) {
                        continuation.resumeWith(Result.success(data.location))
                        true
                    } else {
                        false
                    }
                }
            }
            DefaultArgumentValue(location.latitude to location.longitude)
        } finally {
            removeMessage(msg)
        }
    }


    override fun argument(name: String, message: String?): AnyArgument<String> = DefaultArgument(name) {
        val msg = telegram.sendMessage {
            chatId = _chatId
            markdown2 {
                append(
                        localizationContext.transformStringToLocalized(
                                message ?: "@{bot.abstract.command.please.write.argument}"
                        )
                )
                buildInlineKeyboard {
                    addUserActionButtons(it.isOptional)
                }
            }
        }

        waitResult(msg)
    }

    override fun argument(name: String, variants: List<String>, message: String?): AnyArgument<String> {
        if (variants.isEmpty()) {
            error("Variants can not be empty")
        }

        return DefaultArgument(name) {
            val msg = telegram.sendMessage {
                chatId = _chatId
                markdown2 {
                    append(
                            localizationContext.transformStringToLocalized(
                                    message ?: "@{bot.abstract.command.please.choose.argument}"
                            )
                    )
                }
                buildInlineKeyboard {
                    variants.forEachIndexed { index, it ->
                        line {
                            add {
                                text = localizationContext.transformStringToLocalized(it)
                                callbackData(_chatId, _userId, index)
                            }
                        }
                    }
                    addUserActionButtons(it.isOptional)
                }
            }

            waitResult(msg).transformOrEmpty { data ->
                data
                        .toIntOrNull()
                        ?.let { if (variants.indices.contains(it)) variants[it] else null }

                        ?: variants
                                .filter { it.lowercase() == data.lowercase() }
                                .firstOrNull()
            }
        }
    }

    override fun argument(name: String, variants: Map<String, String>, message: String?): AnyArgument<String> {
        if (variants.isEmpty()) {
            error("Variants can not be empty")
        }

        return DefaultArgument(name) {
            val list = variants.toList()
            val msg = telegram.sendMessage {
                chatId = _chatId
                markdown2 {
                    append(
                            localizationContext.transformStringToLocalized(
                                    message ?: "@{bot.abstract.command.please.choose.argument}"
                            )
                    )
                    buildInlineKeyboard {
                        list.forEachIndexed { index, pair ->
                            line {
                                add {
                                    text = localizationContext.transformStringToLocalized(pair.first)
                                    callbackData(_chatId, _userId, index)
                                }
                            }
                        }
                        addUserActionButtons(it.isOptional)
                    }
                }
            }


            waitResult(msg).transformOrEmpty { data ->
                data
                        .toLongOrNull()
                        ?.let { if (list.indices.contains(it)) list[it.toInt()].second else null }

                        ?: list
                                .filter { it.first.lowercase() == data.lowercase() }
                                .firstOrNull()
                                ?.second
            }
        }
    }

    private fun InlineKeyboardMarkupBuilder.addUserActionButtons(optional: Boolean) {
        if (optional) {
            line {
                addContinueButton()
            }
        }
//        line {
//            add {
//                text = "\u274C"
//                cancelCallback(_chatId, _userId)
//            }
//            if (optional) {
//                addContinueButton()
//            }
//        }
    }

    private fun InlineKeyboardMarkupLineBuilder.addContinueButton() {
        add {
            text = localizationContext.findLocalizedStringByKey("phases.skip") ?: "Skip\u27A1"
            continueCallback(_chatId, _userId)
        }
    }

    private suspend fun waitResult(msg: Message?): ArgumentValue<String> {
        try {
            val data = callbackManager.waitCallback(_chatId, _userId, TIMEOUT_ARGUMENT_MS, TimeUnit.MILLISECONDS)

            return when (data.dataFromCallback) {
                CANCEL_CALLBACK -> {
                    throw CancellationException("Callback was cancelled by user")
                }
                CONTINUE_CALLBACK -> {
                    ArgumentValue.empty()
                }
                else -> {
                    (data.dataFromMessage ?: data.dataFromCallback)?.let { DefaultArgumentValue(it) }
                            ?: ArgumentValue.empty()
                }
            }
        } finally {
            removeMessage(msg)
        }
    }


    private suspend fun removeMessage(message: Message?) {
        //FIXME: Should make an architecture for bot UI
        if (message != null) {
            try {
                telegram.deleteMessage {
                    fromMessage(message)
                }
            } catch (e: Exception) {
                LOGGER.error("Can not delete message: '$message'", e)
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TelegramArgumentManager::class.java)
        private const val TIMEOUT_ARGUMENT_MS: Long = 360_000
    }
}