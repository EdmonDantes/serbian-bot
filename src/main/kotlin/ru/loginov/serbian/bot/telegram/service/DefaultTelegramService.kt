package ru.loginov.serbian.bot.telegram.service

import com.fasterxml.jackson.core.type.TypeReference
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.http.HttpClient
import ru.loginov.serbian.bot.telegram.entity.Message
import ru.loginov.serbian.bot.telegram.entity.TelegramResponse
import ru.loginov.serbian.bot.telegram.entity.Update
import ru.loginov.serbian.bot.telegram.entity.User
import ru.loginov.serbian.bot.telegram.entity.request.GetUpdatesRequest
import ru.loginov.serbian.bot.telegram.entity.request.SendMessageRequest
import ru.loginov.serbian.bot.telegram.exception.ResponseError

@Service
class DefaultTelegramService : TelegramService {

    @Value("\${bot.token}")
    private lateinit var token: String

    @Value("\${bot.server.url}")
    private lateinit var url: String

    @Value("\${bot.long.polling.timeout.sec:5}")
    private var longPollingTimeoutSec: Long = 5

    @Autowired
    private lateinit var client: HttpClient

    override suspend fun getUpdates(offset: Long?): List<Update> = getUpdates {
        this.offset = offset
        this.timeoutSec = longPollingTimeoutSec
    }

    override suspend fun getUpdates(request: GetUpdatesRequest.() -> Unit): List<Update> = GetUpdatesRequest().let {
        request(it)
        client.requestJson<List<Update>>(HttpMethod.Post, "getUpdates", it) ?: emptyList()
    }

    override suspend fun getMe(): User? =
            client.requestJson<User>(HttpMethod.Get, "getMe")

    override suspend fun sendMessage(request: SendMessageRequest.() -> Unit): Message? = SendMessageRequest().let {
        request(it)
        client.requestJson<Message>(HttpMethod.Post, "sendMessage", it)
    }

    private fun <T> checkAnswer(response: TelegramResponse<T>?): T? {
        if (response == null) {
            return null
        }

        if (response.isOk) {
            return response.result
        }

        throw ResponseError(response.errorCode, response.description)
    }

    private fun generateUrl(methodName: String): String = "$url$token/$methodName"

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
    ) : T? {
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
}
