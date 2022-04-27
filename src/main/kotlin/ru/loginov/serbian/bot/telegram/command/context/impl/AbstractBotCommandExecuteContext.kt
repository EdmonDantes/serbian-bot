package ru.loginov.serbian.bot.telegram.command.context.impl

import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.Message
import ru.loginov.telegram.api.entity.Update
import ru.loginov.telegram.api.entity.User
import ru.loginov.telegram.api.request.AnswerCallbackQueryRequest
import ru.loginov.telegram.api.request.DeleteMessageRequest
import ru.loginov.telegram.api.request.GetUpdatesRequest
import ru.loginov.telegram.api.request.SendMessageRequest

abstract class AbstractBotCommandExecuteContext(
        override val telegram: TelegramAPI
) : BotCommandExecuteContext {

    override suspend fun answerCallbackQuery(request: AnswerCallbackQueryRequest.() -> Unit) =
            telegram.answerCallbackQuery(request)

    override suspend fun deleteMessage(request: DeleteMessageRequest.() -> Unit) =
            telegram.deleteMessage(request)

    override suspend fun getMe(): User? =
            telegram.getMe()

    override suspend fun getUpdates(request: GetUpdatesRequest.() -> Unit): List<Update> =
            telegram.getUpdates(request)

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
}