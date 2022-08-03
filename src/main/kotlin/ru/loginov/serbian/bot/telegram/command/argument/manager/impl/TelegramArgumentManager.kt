package ru.loginov.serbian.bot.telegram.command.argument.manager.impl

import io.github.edmondantes.simple.kotlin.callbacks.SimpleCallback
import io.github.edmondantes.simple.kotlin.callbacks.key.wait
import io.github.edmondantes.simple.localization.Localizer
import io.github.edmondantes.simple.localization.context.LocalizationContext
import io.github.edmondantes.simple.localization.impl.localizationKey
import kotlinx.coroutines.CancellationException
import org.slf4j.LoggerFactory
import ru.loginov.serbian.bot.configuration.TelegramCallbackStore
import ru.loginov.serbian.bot.telegram.callback.CANCEL_CALLBACK
import ru.loginov.serbian.bot.telegram.callback.CONTINUE_CALLBACK
import ru.loginov.serbian.bot.telegram.callback.CallbackData
import ru.loginov.serbian.bot.telegram.callback.callbackData
import ru.loginov.serbian.bot.telegram.callback.continueCallback
import ru.loginov.serbian.bot.telegram.command.argument.AnyArgument
import ru.loginov.serbian.bot.telegram.command.argument.ArgumentDefinition
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
        private val callbackManager: TelegramCallbackStore,
        private val localizationContext: LocalizationContext,
        private val localizationManager: Localizer,
        private val _chatId: Long,
        private val _userId: Long?,
) : ArgumentManager<String> {
    override fun choose(name: String, message: String?): AnyArgument<Boolean> =
            DefaultArgument(name) {
                val msg = telegram.sendMessage {
                    chatId = _chatId
                    markdown2 {
                        append(message ?: DEFAULT_MESSAGE_FOR_CHOOSE)
                    }
                    inlineKeyboard {
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
        val menu = localizationManager.supportLanguages.associateBy { lang ->
            localizationManager.localize(lang, localizationKey("language.$lang"))
        }

        argument(name, menu, message).configure(definition).process()
    }

    override fun location(name: String, message: String?): AnyArgument<Pair<Double, Double>> = DefaultArgument(name) {
        val msg = telegram.sendMessage {
            chatId = _chatId
            markdown2 {
                append(message ?: DEFAULT_MESSAGE_FOR_WRITE)
            }
            inlineKeyboard {
                addUserActionButtons(it.isOptional)
            }
        }

        try {

            val location = suspendCoroutine<Location> { continuation ->
                val callback = SimpleCallback<CallbackData> { context ->
                    context?.remove()
                    val data = context?.data
                    if (data == null) {
                        continuation.resumeWithException(CancellationException("Callback was cancelled"))
                    } else if (data.location != null) {
                        continuation.resumeWith(Result.success(data.location))
                    }
                }
                callbackManager.add(
                        _chatId to _userId,
                        callback,
                        0,
                        TimeUnit.MILLISECONDS.toMicros(TIMEOUT_ARGUMENT_MS)
                )
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
                append(message ?: DEFAULT_MESSAGE_FOR_WRITE)
            }
            inlineKeyboard {
                addUserActionButtons(it.isOptional)
            }
        }

        waitResult(msg)
    }

    override fun argument(name: String, variants: List<String>, message: String?): AnyArgument<String> {
        if (variants.isEmpty()) {
            error("Variants can not be empty")
        }

        return DefaultArgument(name) {
            argumentGetValue(it, variants, message)
        }
    }

    override fun argument(name: String, variants: Map<String, String>, message: String?): AnyArgument<String> {
        if (variants.isEmpty()) {
            error("Variants can not be empty")
        }

        return DefaultArgument(name) {
            argumentGetValue(it, variants.keys.toList(), message).transformOrEmpty { data -> variants[data] }
        }
    }

    private suspend fun argumentGetValue(
            definition: ArgumentDefinition,
            variants: List<String>,
            message: String?
    ): ArgumentValue<String> {

        val lstMsg = telegram.sendMessageWithoutLimit {
            chatId = _chatId
            markdown2 {
                append(message ?: DEFAULT_MESSAGE_FOR_CHOOSE)
                append('\n')
                append('\n')
                variants.forEachIndexed { index, variant ->
                    if (index != 0) {
                        append('\n')
                        append('\n')
                    }
                    append(index)
                    append('.')
                    append(' ')
                    append(variant)
                }
            }
            inlineKeyboard {
                addChooseIndexButton(0, variants.lastIndex)
                addUserActionButtons(definition.isOptional)
            }
        }

//        val messagesTexts: MutableList<Markdown2StringBuilder> = ArrayList(variants.size)
//
//        messagesTexts.add(DefaultMarkdown2StringBuilder().apply {
//            append(message ?: DEFAULT_MESSAGE_FOR_CHOOSE)
//        })
//
//        for (index in 0 until variants.size - 1) {
//            val variant = variants[index]
//            messagesTexts.add(DefaultMarkdown2StringBuilder().apply {
//                append("$index. ")
//                append(variant)
//            })
//        }
//
//        val messagesFutures = messagesTexts.map {
//            coroutineScope {
//                async {
//                    telegram.sendMessage {
//                        chatId = _chatId
//                        markdown2(it)
//                    }
//                }
//            }
//        }.plus(
//                coroutineScope {
//                    async {
//                        val index = variants.lastIndex
//                        telegram.sendMessage {
//                            chatId = _chatId
//                            markdown2 {
//                                append("$index. ")
//                                append(variants[index])
//                            }
//                            inlineKeyboard {
//                                addChooseIndexButton(0, index)
//                                addUserActionButtons(definition.isOptional)
//                            }
//                        }
//                    }
//                }
//        )
//
//        val messages = messagesFutures.map { it.await() }
//
//        if (messages.size - 1 != variants.size) {
//            telegram.sendMessage {
//                chatId = _chatId
//                markdown2 {
//                    append(
//                            localizationContext.localizeOrDefault(
//                                    localizationKey("phases.internal.error"),
//                                    "Internal error"
//                            )
//                    )
//                }
//            }
//
//            error("Can not send messages with all variants. Variants count '${variants.size}'. Send message count '${messages.size}'")
//        }

        var value: ArgumentValue<String>
        var lastMessage: Message? = lstMsg.last()//messages.last()
        var page = 0

        try {
            do {
                value = waitCallback()

                if (value.isEmpty) {
                    break
                }

                when (value.value) {
                    "next" -> {
                        page++
                    }
                    "prev" -> {
                        page--
                    }
                    else -> break
                }

                lastMessage = telegram.editMessageReplyMarkup {
                    chatId = _chatId
                    messageId = lastMessage?.id
                    inlineKeyboard {
                        addChooseIndexButton(0, variants.lastIndex, page)
                        addUserActionButtons(definition.isOptional)
                    }
                }
            } while (lastMessage != null)

            return value.transformOrEmpty { data ->
                data
                        .toIntOrNull()
                        ?.let { if (variants.indices.contains(it)) variants[it] else null }

                        ?: variants.firstOrNull { it.lowercase() == data.lowercase() }

            }
        } finally {
            lstMsg.forEach {
                removeMessage(it)
            }
        }
    }

    private fun InlineKeyboardMarkupBuilder.addChooseIndexButton(startIndex: Int, lastIndex: Int, page: Int = 0) {
        if (startIndex > lastIndex) {
            throw IllegalArgumentException("Wrong indexes. Start indexes should be less than last index. Start: '$startIndex'. Last: '$lastIndex'")
        }

        val pages = (startIndex..lastIndex).windowed(MAX_MENU_ELEMENTS_ON_PAGE, MAX_MENU_ELEMENTS_ON_PAGE, true)

        if (!pages.indices.contains(page)) {
            throw IllegalArgumentException("Wrong page. Start: '$startIndex'. Last: '$lastIndex'. Page: '$page'")
        }

        val elements = pages[page]

        val needButtonForPrevPage = page != 0
        val needButtonForNextPage = page < pages.lastIndex
        val countOnLine =
                if (elements.size < MAX_MENU_ELEMENTS_ON_LINE)
                    MAX_MENU_ELEMENTS_ON_LINE
                else
                    (elements.size + 1) / 2

        var i = 0
        repeat(2) { lineNumber ->
            line {
                while (i < countOnLine * (lineNumber + 1) && i < elements.size) {
                    val element = elements[i]
                    add {
                        text = "$element"
                        callbackData(_chatId, _userId, element)
                    }
                    i++
                }
            }
        }

        if (needButtonForPrevPage || needButtonForNextPage) {
            line {
                if (needButtonForPrevPage) {
                    add {
                        text = localizationContext.localizeOrDefault(
                                localizationKey("phases.prev.page"),
                                "Previous page\u2B05"
                        )
                        callbackData(_chatId, _userId, "prev")
                    }
                }
                if (needButtonForNextPage) {
                    add {
                        text = localizationContext.localizeOrDefault(
                                localizationKey("phases.next.page"),
                                "Next page\u21A1"
                        )
                        callbackData(_chatId, _userId, "next")
                    }
                }
            }
        }
    }

    private fun InlineKeyboardMarkupBuilder.addUserActionButtons(optional: Boolean) {
        if (optional) {
            line {
                addContinueButton()
            }
        }
    }

    private fun InlineKeyboardMarkupLineBuilder.addContinueButton() {
        add {
            text = localizationContext.localizeOrDefault(localizationKey("phases.skip"), "Skip\u27A1")
            continueCallback(_chatId, _userId)
        }
    }

    private suspend fun waitCallback(): ArgumentValue<String> {
        val data = callbackManager.wait(
                _chatId to _userId,
                0,
                TimeUnit.MILLISECONDS.toMicros(TIMEOUT_ARGUMENT_MS)
        )

        return when (data.data) {
            CANCEL_CALLBACK -> throw CancellationException("Callback was cancelled by user")
            CONTINUE_CALLBACK -> ArgumentValue.empty()
            else -> data.data?.let { DefaultArgumentValue(it) } ?: ArgumentValue.empty()

        }
    }

    private suspend fun waitResult(msg: Message?): ArgumentValue<String> {
        try {
            return waitCallback()
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

        private const val MAX_MENU_COUNT_LINES = 2
        private const val MAX_MENU_ELEMENTS_ON_LINE = 5
        private const val MAX_MENU_ELEMENTS_ON_PAGE = MAX_MENU_ELEMENTS_ON_LINE * MAX_MENU_COUNT_LINES

        private const val DEFAULT_MESSAGE_FOR_WRITE = "Please write argument"
        private const val DEFAULT_MESSAGE_FOR_CHOOSE = "Please choose argument"
    }
}