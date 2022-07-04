package ru.loginov.serbian.bot.telegram.command.context.impl

import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext
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

open class ParentBotCommandExecuteContext(protected val parent: BotCommandExecuteContext) : BotCommandExecuteContext {
    override val arguments: ArgumentManager<LocalizationRequest>
        get() = parent.arguments
    override val localization: LocalizationContext
        get() = parent.localization
    override val inputMessage: Message
        get() = parent.inputMessage
    override val user: UserDto
        get() = parent.user
    override val chatId: Long
        get() = parent.chatId

    override fun hasPermission(permission: String): Boolean =
            parent.hasPermission(permission)

    override fun hasAllPermissions(permissions: List<String>): Boolean =
            parent.hasAllPermissions(permissions)

    override suspend fun answerCallbackQuery(request: AnswerCallbackQueryRequest.() -> Unit) =
            parent.answerCallbackQuery(request)

    override suspend fun deleteMessage(request: DeleteMessageRequest.() -> Unit) =
            parent.deleteMessage(request)

    override suspend fun editMessageReplyMarkup(request: EditMessageReplyMarkupRequest.() -> Unit): Message? =
            parent.editMessageReplyMarkup(request)

    override suspend fun getMyCommands(request: GetMyCommandsRequest.() -> Unit): List<BotCommand> =
            parent.getMyCommands(request)

    override suspend fun getMe(): User? =
            parent.getMe()

    override suspend fun getUpdates(request: GetUpdatesRequest.() -> Unit): List<Update> =
            parent.getUpdates(request)

    override suspend fun setMyCommands(request: SetMyCommandsRequest.() -> Unit) =
            parent.setMyCommands(request)

    override suspend fun sendMessage(request: SendMessageRequest.() -> Unit): Message? =
            parent.sendMessage(request)

    override suspend fun sendMessageWithoutLimit(request: SendMessageRequest.() -> Unit): List<Message> =
            parent.sendMessageWithoutLimit(request)
}