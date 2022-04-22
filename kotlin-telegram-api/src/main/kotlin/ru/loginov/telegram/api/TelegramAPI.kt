package ru.loginov.telegram.api

import ru.loginov.telegram.api.entity.Message
import ru.loginov.telegram.api.entity.Update
import ru.loginov.telegram.api.entity.User
import ru.loginov.telegram.api.request.GetUpdatesRequest
import ru.loginov.telegram.api.request.SendMessageRequest

interface TelegramAPI {

    suspend fun getMe() : User?
    suspend fun getUpdates(request: GetUpdatesRequest.() -> Unit) : List<Update>
    suspend fun sendMessage(request: SendMessageRequest.() -> Unit) : Message?

}