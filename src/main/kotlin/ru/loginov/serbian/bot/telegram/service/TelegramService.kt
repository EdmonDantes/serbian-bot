package ru.loginov.serbian.bot.telegram.service

import ru.loginov.serbian.bot.telegram.entity.Message
import ru.loginov.serbian.bot.telegram.entity.Update
import ru.loginov.serbian.bot.telegram.entity.User
import ru.loginov.serbian.bot.telegram.entity.request.GetUpdatesRequest
import ru.loginov.serbian.bot.telegram.entity.request.SendMessageRequest

interface TelegramService {

    suspend fun getUpdates(offset: Long?) : List<Update>
    suspend fun getUpdates(request: GetUpdatesRequest.() -> Unit) : List<Update>
    suspend fun getMe() : User?
    suspend fun sendMessage(request: SendMessageRequest.() -> Unit) : Message?

}