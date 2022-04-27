package ru.loginov.telegram.api

import com.fasterxml.jackson.core.type.TypeReference
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import ru.loginov.http.HttpClient
import ru.loginov.telegram.api.entity.Message
import ru.loginov.telegram.api.entity.Update
import ru.loginov.telegram.api.entity.User
import ru.loginov.telegram.api.exception.ResponseErrorException
import ru.loginov.telegram.api.request.AnswerCallbackQueryRequest
import ru.loginov.telegram.api.request.DeleteMessageRequest
import ru.loginov.telegram.api.request.GetUpdatesRequest
import ru.loginov.telegram.api.request.SendMessageRequest
import ru.loginov.telegram.api.response.TelegramResponse

class DefaultTelegramAPI(
        private val client: HttpClient,
        private val baseUrl: String,
) : TelegramAPI {
    override suspend fun answerCallbackQuery(request: AnswerCallbackQueryRequest.() -> Unit) {
        client.requestJson<Boolean>(
                HttpMethod.Post,
                "answerCallbackQuery",
                AnswerCallbackQueryRequest().also(request)
        )
    }

    override suspend fun deleteMessage(request: DeleteMessageRequest.() -> Unit) {
        client.requestJson<Boolean>(HttpMethod.Post, "deleteMessage", DeleteMessageRequest().also(request))
    }

    override suspend fun getMe(): User? =
            client.requestJson<User>(HttpMethod.Get, "getMe")

    override suspend fun getUpdates(request: GetUpdatesRequest.() -> Unit): List<Update> =
            GetUpdatesRequest().let {
                request(it)
                client.requestJson<List<Update>>(HttpMethod.Post, "getUpdates", it) ?: emptyList()
            }

    override suspend fun sendMessage(request: SendMessageRequest.() -> Unit): Message? =
            SendMessageRequest().let {
                request(it)
                client.requestJson<Message>(HttpMethod.Post, "sendMessage", it)
            }

    override suspend fun sendMessageWithoutLimit(request: SendMessageRequest.() -> Unit): List<Message> {
        val request = SendMessageRequest().also(request)

        if (request.text == null) {
            return client.requestJson<Message>(HttpMethod.Post, "sendMessage", request)?.let { listOf(it) }
                    ?: emptyList()
        }

        val result = ArrayList<Deferred<Message?>>()
        var index = 0
        coroutineScope {
            while (request.text!!.length - index > 0) {
                val msg = async {
                    sendMessage {
                        chatId = request.chatId
                        disableWebPagePreview = request.disableWebPagePreview
                        disableNotification = request.disableNotification
                        protectContent = request.protectContent
                        allowSendingWithoutReply = request.allowSendingWithoutReply
                        if (index == 0) {
                            replyToMessageId = request.replyToMessageId
                        }

                        val prepareText = request.text!!.substring(index, index + MAX_MESSAGE_TEXT_LENGTH)
                        val lastIndex = prepareText.lastIndexOf('\n')
                                .let { if (it != -1) it else prepareText.lastIndexOf(' ') }
                                .let { if (it != -1) it else prepareText.length }

                        text = prepareText.substring(0, lastIndex)
                        index += lastIndex + 1

                        if (index >= request.text!!.length) {
                            keyboard = request.keyboard
                        }
                    }
                }
                result.add(msg)
            }
        }

        return result.mapNotNull { it.await() }
    }

    private fun <T> checkAnswer(response: TelegramResponse<T>?): T? {
        if (response == null) {
            return null
        }

        if (response.isSuccess) {
            return response.result
        }

        throw ResponseErrorException(response.description)
    }

    private fun generateUrl(methodName: String): String = "$baseUrl/$methodName"

    private suspend inline fun <reified T> HttpClient.requestJson(
            method: HttpMethod,
            methodName: String,
            body: Any? = null,
    ): T? {
        val response = requestJson(
                method,
                generateUrl(methodName),
                object : TypeReference<TelegramResponse<T>>() {},
                body,
                emptyMap()
        )
        return checkAnswer(response.first)
    }

    private suspend inline fun <reified T> HttpClient.requestWithJsonResponse(
            method: HttpMethod,
            methodName: String,
            body: ByteArray? = null,
            queryParameters: Map<String, String> = emptyMap(),
            contentType: ContentType? = null
    ): T? {
        val response = requestWithJsonResponse(
                method,
                generateUrl(methodName),
                object : TypeReference<TelegramResponse<T>>() {},
                body,
                queryParameters,
                contentType
        )

        return checkAnswer(response.first)
    }

    companion object {
        private const val MAX_MESSAGE_TEXT_LENGTH = 4096
    }
}