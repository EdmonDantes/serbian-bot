package ru.loginov.serbian.bot.telegram.command.context.impl

import org.slf4j.LoggerFactory
import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext
import ru.loginov.simple.permissions.PermissionOwner
import ru.loginov.simple.permissions.manager.PermissionManager
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.BotCommand
import ru.loginov.telegram.api.entity.Message
import ru.loginov.telegram.api.entity.Update
import ru.loginov.telegram.api.entity.User
import ru.loginov.telegram.api.request.AnswerCallbackQueryRequest
import ru.loginov.telegram.api.request.DeleteMessageRequest
import ru.loginov.telegram.api.request.EditMessageReplyMarkupRequest
import ru.loginov.telegram.api.request.GetMyCommandsRequest
import ru.loginov.telegram.api.request.GetUpdatesRequest
import ru.loginov.telegram.api.request.SendMessageRequest
import ru.loginov.telegram.api.request.SetMyCommandsRequest

// We can not split implementation for different classes, because class can extend only one another class
abstract class AbstractBotCommandExecuteContext(
        private val telegram: TelegramAPI,
        private val permissionManager: PermissionManager,
        override val localization: LocalizationContext,
        override val arguments: ArgumentManager<LocalizationRequest>,
        final override val chatId: Long,
        final override val user: UserDto,
        final override val inputMessage: Message
) : BotCommandExecuteContext {

    // Permission context implementation
    override fun hasPermission(permission: String): Boolean {
        val owner = permissionManager.getOwnerForGroupOrDefault(user.permissionGroup) ?: PermissionOwner.NO_PERMISSION
        return owner.checkPermission(permission.lowercase())
    }

    override fun hasAllPermissions(permissions: List<String>): Boolean {
        val owner = permissionManager.getOwnerForGroupOrDefault(user.permissionGroup) ?: PermissionOwner.NO_PERMISSION
        return owner.checkAllPermission(permissions)
    }

    // Telegram API implementation
    override suspend fun answerCallbackQuery(request: AnswerCallbackQueryRequest.() -> Unit) =
            telegram.answerCallbackQuery(request)

    override suspend fun editMessageReplyMarkup(request: EditMessageReplyMarkupRequest.() -> Unit): Message? {
        val _chatId = chatId
        return telegram.editMessageReplyMarkup {
            chatId = _chatId
            request(this)
        }
    }

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
