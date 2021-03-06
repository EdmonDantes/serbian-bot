package ru.loginov.serbian.bot.telegram.entity.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import ru.loginov.serbian.bot.telegram.entity.InlineKeyboardMarkupBuilder
import ru.loginov.serbian.bot.telegram.entity.MessageEntity
import ru.loginov.serbian.bot.telegram.entity.ReplyKeyboardMarkupBuilder
import ru.loginov.serbian.bot.telegram.entity.ReplyKeyboardRemove
import ru.loginov.serbian.bot.telegram.util.StringBuilderMarkdownV2

/**
 * This object represents request for method [ru.loginov.serbian.bot.telegram.service.TelegramService.sendMessage]
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class SendMessageRequest {
    /**
     * Unique identifier for the target chat
     */
    @JsonProperty(value = "chat_id", required = true)
    var chatId: Long? = null

    /**
     * Text of the message to be sent, 1-4096 characters after entities parsing
     */
    @JsonProperty(value = "text", required = true)
    var text: String? = null

    /**
     * Mode for parsing entities in the message text.
     *
     * *Optional*
     */
    @JsonProperty(value = "parse_mode", required = false)
    var parseMode: String? = null

    /**
     * A JSON-serialized list of special entities that appear in message text,
     * which can be specified instead of [parseMode]
     *
     * *Optional*
     */
    @JsonProperty(value = "entities", required = false)
    var entities: List<MessageEntity>? = null

    /**
     * Disables link previews for links in this message
     *
     * *Optional*
     */
    @JsonProperty(value = "disable_web_page_preview", required = false)
    var disableWebPagePreview: Boolean? = null

    /**
     * Sends the message silently. Users will receive a notification with no sound.
     *
     * *Optional*
     */
    @JsonProperty(value = "disable_notification", required = false)
    var disableNotification: Boolean? = null

    /**
     * Protects the contents of the sent message from forwarding and saving
     *
     * *Optional*
     */
    @JsonProperty(value = "protect_content", required = false)
    var protectContent: Boolean? = null

    /**
     * If the message is a reply, ID of the original message
     *
     * *Optional*
     */
    @JsonProperty(value = "reply_to_message_id", required = false)
    var replyToMessageId: Long? = null

    /**
     * Pass True, if the message should be sent even if the specified replied-to message is not found
     *
     * *Optional*
     */
    @JsonProperty(value = "allow_sending_without_reply", required = false)
    var allowSendingWithoutReply: Boolean? = null

    /**
     * Additional interface options. A JSON-serialized object for an inline keyboard,
     * custom reply keyboard, instructions to remove reply keyboard or to force a reply from the user.
     *
     * *Optional*
     */
    @JsonProperty(value = "reply_markup", required = false)
    var keyboard: Any? = null


    fun buildText(block: StringBuilderMarkdownV2.() -> Unit): SendMessageRequest = apply {
        val builder = StringBuilderMarkdownV2()
        block(builder)
        text = builder.toMarkdownV2String()
        parseMode = "MarkdownV2"
    }

    fun silence(): SendMessageRequest = apply {
        disableNotification = true
    }

    fun protect() : SendMessageRequest = apply {
        protectContent = true
    }

    fun disablePreview() : SendMessageRequest = apply {
        disableWebPagePreview = false
    }

    fun reply(messageId: Long) : SendMessageRequest = apply {
        replyToMessageId = messageId
        allowSendingWithoutReply = true
    }

    fun buildInlineKeyboard(block: InlineKeyboardMarkupBuilder.() -> Unit) : SendMessageRequest = apply {
        val builder = InlineKeyboardMarkupBuilder()
        block(builder)
        keyboard = builder.build()
    }

    fun buildReplyKeyboard(block: ReplyKeyboardMarkupBuilder.() -> Unit) : SendMessageRequest = apply {
        val builder = ReplyKeyboardMarkupBuilder()
        block(builder)
        keyboard = builder.build()
    }

    fun buildRemoveKeyboard(selective: Boolean = false) : SendMessageRequest = apply {
        keyboard = ReplyKeyboardRemove(true, selective)
    }

    fun buildForceReply() : SendMessageRequest = apply {
        TODO("Not yet support")
    }
}
