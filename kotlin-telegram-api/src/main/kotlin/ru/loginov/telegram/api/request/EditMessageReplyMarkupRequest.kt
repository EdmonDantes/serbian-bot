package ru.loginov.telegram.api.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import ru.loginov.telegram.api.entity.InlineKeyboardMarkup
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupBuilder

/**
 * This object represents request for method [ru.loginov.telegram.api.TelegramAPI.editMessageReplyMarkup]
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class EditMessageReplyMarkupRequest {

    @JsonProperty(value = "chat_id", required = false)
    var chatId: Long? = null

    @JsonProperty(value = "message_id", required = false)
    var messageId: Long? = null

    @JsonProperty(value = "inline_message_id", required = false)
    var inlineMessageId: Long? = null

    @JsonProperty(value = "reply_markup", required = false)
    var keyboard: InlineKeyboardMarkup? = null

    fun inlineKeyboard(block: InlineKeyboardMarkupBuilder.() -> Unit): EditMessageReplyMarkupRequest = apply {
        keyboard = InlineKeyboardMarkupBuilder().also(block).build()
    }

}