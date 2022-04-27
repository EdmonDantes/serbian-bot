package ru.loginov.telegram.api.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import ru.loginov.telegram.api.entity.Message

/**
 * This object represents request for method [ru.loginov.telegram.api.TelegramAPI.deleteMessage]
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class DeleteMessageRequest {

    /**
     * Unique identifier for the target chat or username of the target channel
     */
    @JsonProperty(value = "chat_id", required = true)
    var chatId: Long? = null

    /**
     * Identifier of the message to delete
     */
    @JsonProperty(value = "message_id", required = true)
    var messageId: Long? = null

    fun fromMessage(message: Message) {
        this.chatId = message.chat.id
        this.messageId = message.id
    }
}