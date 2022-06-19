package ru.loginov.serbian.bot.telegram.command.context.impl

import org.slf4j.LoggerFactory
import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.telegram.callback.TelegramCallbackManager
import ru.loginov.serbian.bot.telegram.command.argument.AnyArgument
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.StringArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.TelegramArgumentManager
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.simple.permissions.PermissionOwner
import ru.loginov.simple.permissions.manager.PermissionManager
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.BotCommand
import ru.loginov.telegram.api.entity.Message
import ru.loginov.telegram.api.entity.Update
import ru.loginov.telegram.api.entity.User
import ru.loginov.telegram.api.request.AnswerCallbackQueryRequest
import ru.loginov.telegram.api.request.DeleteMessageRequest
import ru.loginov.telegram.api.request.GetMyCommandsRequest
import ru.loginov.telegram.api.request.GetUpdatesRequest
import ru.loginov.telegram.api.request.SendMessageRequest
import ru.loginov.telegram.api.request.SetMyCommandsRequest

// We can not split implementation for different classes, because class can extend only one another class
abstract class AbstractBotCommandExecuteContext(
        private val telegram: TelegramAPI,
        private val permissionManager: PermissionManager,
        private val localizationManager: LocalizationManager,
        private val callbackManager: TelegramCallbackManager,
        override final val chatId: Long,
        override final val user: UserDto,
        messageWithoutCommand: String
) : BotCommandExecuteContext {

    private val telegramArgumentManager: ArgumentManager = TelegramArgumentManager(
            telegram,
            callbackManager,
            this,
            localizationManager,
            chatId,
            user.id
    )
    private val stringArgumentManager: ArgumentManager = StringArgumentManager(
            telegramArgumentManager,
            messageWithoutCommand
    )


    // Permission context implementation
    override fun hasPermission(permission: String): Boolean {
        val owner = permissionManager.getOwnerForGroupOrDefault(user.permissionGroup) ?: PermissionOwner.NO_PERMISSION
        return owner.checkPermission(permission.lowercase())
    }

    override fun hasAllPermissions(permissions: List<String>): Boolean {
        val owner = permissionManager.getOwnerForGroupOrDefault(user.permissionGroup) ?: PermissionOwner.NO_PERMISSION
        return owner.checkAllPermission(permissions)
    }

    // Localization context implementation
    override fun findLocalizedStringByKey(str: String): String? =
            localizationManager.findLocalizedStringByKey(user.language, str)

    override fun transformStringToLocalized(str: String): String =
            localizationManager.transformStringToLocalized(user.language, str)

    // BotCommandArgumentManager implementation
    override fun choose(name: String, message: String?): AnyArgument<Boolean> =
            stringArgumentManager.choose(name, message)


    override fun language(name: String, message: String?): AnyArgument<String> =
            stringArgumentManager.language(name, message)

    override fun location(name: String, message: String?): AnyArgument<Pair<Double, Double>> =
            stringArgumentManager.location(name, message)

    override fun argument(name: String, message: String?): AnyArgument<String> =
            stringArgumentManager.argument(name, message)

    override fun argument(name: String, variants: List<String>, message: String?): AnyArgument<String> =
            stringArgumentManager.argument(name, variants, message)

    override fun argument(name: String, variants: Map<String, String>, message: String?): AnyArgument<String> =
            stringArgumentManager.argument(name, variants, message)


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

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AbstractBotCommandExecuteContext::class.java)
    }
}
