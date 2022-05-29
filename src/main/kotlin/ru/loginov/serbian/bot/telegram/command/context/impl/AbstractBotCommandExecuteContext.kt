package ru.loginov.serbian.bot.telegram.command.context.impl

import org.slf4j.LoggerFactory
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.data.manager.permission.PermissionManager
import ru.loginov.serbian.bot.spring.permission.exception.NotFoundPermissionException
import ru.loginov.serbian.bot.telegram.callback.TelegramCallbackManager
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.BotCommand
import ru.loginov.telegram.api.entity.Location
import ru.loginov.telegram.api.entity.Message
import ru.loginov.telegram.api.entity.Update
import ru.loginov.telegram.api.entity.User
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupBuilder
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupButtonBuilder
import ru.loginov.telegram.api.request.AnswerCallbackQueryRequest
import ru.loginov.telegram.api.request.DeleteMessageRequest
import ru.loginov.telegram.api.request.GetMyCommandsRequest
import ru.loginov.telegram.api.request.GetUpdatesRequest
import ru.loginov.telegram.api.request.SendMessageRequest
import ru.loginov.telegram.api.request.SetMyCommandsRequest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// We can not split implementation for different classes, because class can extend only one another class
abstract class AbstractBotCommandExecuteContext(
        private val telegram: TelegramAPI,
        private val permissionManager: PermissionManager,
        private val localizationManager: LocalizationManager,
        private val callbackManager: TelegramCallbackManager,
        messageWithoutCommand: String
) : BotCommandExecuteContext {

    private val arguments: List<String>
    private var index: Int = 0

    init {
        val arguments = ArrayList<String>()
        var builder = StringBuilder()
        var ignoreSpaces = false
        messageWithoutCommand.forEachIndexed { index, ch ->
            if (ch == '\'' && (index == 0 || messageWithoutCommand[index - 1] != '\\')) {
                if (ignoreSpaces) {
                    arguments.add(builder.toString())
                    builder = StringBuilder()
                } else {
                    ignoreSpaces = true
                }
            } else if (ch == ' ') {
                if (ignoreSpaces) {
                    builder.append(ch)
                } else if (builder.isNotEmpty()) {
                    arguments.add(builder.toString())
                    builder = StringBuilder()
                }
            } else {
                builder.append(ch)
            }
        }

        if (builder.isNotEmpty()) {
            arguments.add(builder.toString())
        }

        this.arguments = arguments
    }

    // Permission context implementation

    override fun havePermission(permission: String): Boolean {
        val tree = permissionManager.getPermissionsForUser(user) ?: throw NotFoundPermissionException(user)
        return tree.havePermission(permission.lowercase())
    }

    override fun haveAllPermissions(permissions: List<String>): Boolean {
        val tree = permissionManager.getPermissionsForUser(user) ?: throw NotFoundPermissionException(user)
        return permissions.all { tree.havePermission(it.lowercase()) }
    }

    // Localization context implementation

    override fun findLocalizedStringByKey(str: String): String? =
            localizationManager.findLocalizedStringByKey(user.language, str)

    override fun transformStringToLocalized(str: String): String =
            localizationManager.transformStringToLocalized(user.language, str)

    // BotCommandArgumentManager implementation

    override suspend fun getNextChooseArgument(message: String?): Boolean =
            getNextArgumentFromMessage()?.toBooleanStrictOrNull()
                    ?: sendMessage {
                        markdown2 {
                            append(
                                    transformStringToLocalized(
                                            message ?: "@{bot.abstract.command.please.choose.argument}"
                                    )
                            )
                            buildInlineKeyboard {
                                line {
                                    add {
                                        text = "\u2705"
                                        callbackData(chatId!!, user.id, 1)
                                    }
                                    add {
                                        text = "\u274C"
                                        callbackData(chatId!!, user.id, 2)
                                    }
                                }
                            }
                        }
                    }.let { msg ->
                        try {
                            waitResult()?.toIntOrNull() == 1
                        } finally {
                            removeMessage(msg)
                        }
                    }

    override suspend fun getNextLanguageArgument(message: String?, optional: Boolean): String? {
        val menu = localizationManager.supportLanguages.mapNotNull { lang ->
            localizationManager.findLocalizedStringByKey(
                    lang,
                    "language.$lang"
            )?.let { it to lang }
        }.toMap()

        return getNextArgument(menu, message, optional)
    }

    override suspend fun getNextLocationArgument(message: String?, optional: Boolean): Pair<Double, Double>? =
            getNextArgumentFromMessage()?.toDoubleOrNull()?.let { first ->
                getNextArgumentFromMessage()?.toDoubleOrNull()?.let { second ->
                    first to second
                }
            } ?: sendMessage {
                markdown2 {
                    append(transformStringToLocalized(message ?: "@{bot.abstract.command.please.write.argument}"))
                    buildInlineKeyboard {
                        addUserActionButtons(optional)
                    }
                }
            }.let { msg ->
                try {
                    val location = suspendCoroutine<Location> { continuation ->
                        callbackManager.addCallback(chatId, user.id, 360_000, null) { data ->
                            if (data == null) {
                                continuation.resumeWithException(CancellationException("Callback is canceled", null))
                                true
                            } else if (data.location != null) {
                                continuation.resumeWith(Result.success(data.location))
                                true
                            } else {
                                false
                            }
                        }
                    }
                    location.latitude to location.longitude
                } finally {
                    removeMessage(msg)
                }
            }

    override suspend fun getNextArgument(message: String?, optional: Boolean): String? =
            getNextArgumentFromMessage()
                    ?: sendMessage {
                        markdown2 {
                            append(
                                    transformStringToLocalized(
                                            message ?: "@{bot.abstract.command.please.write.argument}"
                                    )
                            )
                            buildInlineKeyboard {
                                addUserActionButtons(optional)
                            }
                        }
                    }.let { msg ->
                        try {
                            waitResult()
                        } finally {
                            removeMessage(msg)
                        }
                    }


    override suspend fun getNextArgument(variants: List<String>, message: String?, optional: Boolean): String? {
        val result = getNextArgumentFromMessage()
        if (result != null) {
            return result
        }

        if (variants.isEmpty()) {
            return null
        }

        val msg = sendMessage {
            markdown2 {
                append(transformStringToLocalized(message ?: "@{bot.abstract.command.please.choose.argument}"))
                buildInlineKeyboard {
                    variants.forEachIndexed { index, it ->
                        line {
                            add {
                                text = transformStringToLocalized(it)
                                callbackData(chatId!!, user.id, index)
                            }
                        }
                    }
                    addUserActionButtons(optional)
                }
            }
        }

        try {
            return waitResult()
                    ?.let { data ->
                        data.toIntOrNull()?.let { if (variants.indices.contains(it)) variants[it] else null }
                                ?: variants.filter { it.lowercase() == data.lowercase() }.firstOrNull()
                    }
        } finally {
            removeMessage(msg)
        }
    }

    override suspend fun getNextArgument(variants: Map<String, String>, message: String?, optional: Boolean): String? {
        val result = getNextArgumentFromMessage()
        if (result != null) {
            return result
        }

        if (variants.isEmpty()) {
            return null
        }

        val list = variants.toList()
        val msg = sendMessage {
            markdown2 {
                append(transformStringToLocalized(message ?: "@{bot.abstract.command.please.choose.argument}"))
                buildInlineKeyboard {
                    list.forEachIndexed { index, pair ->
                        line {
                            add {
                                text = transformStringToLocalized(pair.first)
                                callbackData(chatId!!, user.id, index)
                            }
                        }
                    }
                    addUserActionButtons(optional)
                }
            }
        }

        try {
            return waitResult()?.let { data ->
                data.toLongOrNull()?.let { if (list.indices.contains(it)) list[it.toInt()].second else null }
                        ?: list.filter { it.first.lowercase() == data.lowercase() }.firstOrNull()?.second
            }
        } finally {
            removeMessage(msg)
        }
    }

    override suspend fun answerCallbackQuery(request: AnswerCallbackQueryRequest.() -> Unit) =
            telegram.answerCallbackQuery(request)

    override suspend fun deleteMessage(request: DeleteMessageRequest.() -> Unit) =
            telegram.deleteMessage(request)

    override suspend fun getMyCommands(request: GetMyCommandsRequest.() -> Unit): List<BotCommand> =
            telegram.getMyCommands(request)

    override suspend fun getMe(): User? =
            telegram.getMe()

    override suspend fun getUpdates(request: GetUpdatesRequest.() -> Unit): List<Update> =
            telegram.getUpdates(request)

    override suspend fun setMyCommands(request: SetMyCommandsRequest.() -> Unit) =
            telegram.setMyCommands(request)

    override suspend fun sendMessage(request: SendMessageRequest.() -> Unit): Message? {
        val _chatId = chatId
        return telegram.sendMessage {
            this.chatId = _chatId
            request(this)
        }
    }

    override suspend fun sendMessageWithoutLimit(request: SendMessageRequest.() -> Unit): List<Message> {
        val _chatId = chatId
        return telegram.sendMessageWithoutLimit {
            this.chatId = _chatId
            request(this)
        }
    }

    private fun getNextArgumentFromMessage(): String? =
            if (index < arguments.size) {
                arguments[index++]
            } else {
                null
            }

    private suspend fun waitResult(): String? {
        //TODO: Extract timeout to constant and add time unit
        val data = callbackManager.waitCallback(chatId, user.id, 360_000)
        return when (data.dataFromCallback) {
            InlineKeyboardMarkupButtonBuilder.CANCEL_CALLBACK -> {
                throw CancellationException()
            }
            InlineKeyboardMarkupButtonBuilder.CONTINUE_CALLBACK -> {
                null
            }
            else -> {
                data.dataFromMessage ?: data.dataFromCallback
            }
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

    private fun InlineKeyboardMarkupBuilder.addUserActionButtons(optional: Boolean) {
        line {
            add {
                text = "\u274C"
                cancelCallback(chatId, user.id)
            }
            if (optional) {
                add {
                    text = "\u27A1"
                    continueCallback(chatId, user.id)
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AbstractBotCommandExecuteContext::class.java)
    }
}
