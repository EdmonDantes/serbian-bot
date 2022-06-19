package ru.loginov.telegram.api.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import ru.loginov.telegram.api.entity.MessageEntity
import ru.loginov.telegram.api.entity.ParseMode
import ru.loginov.telegram.api.entity.ReplyKeyboardRemove
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupBuilder
import ru.loginov.telegram.api.entity.builder.ReplyKeyboardMarkupBuilder
import ru.loginov.telegram.api.util.Markdown2StringBuilder
import ru.loginov.telegram.api.util.impl.DefaultMarkdown2StringBuilder

/**
 * This object represents request for method [ru.loginov.telegram.api.TelegramAPI.sendMessage]
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
    //TODO: Change to enum
    @JsonProperty(value = "parse_mode", required = false)
    var parseMode: ParseMode? = null

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

    fun markdown2(
            builder: Markdown2StringBuilder = DefaultMarkdown2StringBuilder(),
            block: Markdown2StringBuilder.() -> Unit
    ): SendMessageRequest = apply {
        block(builder)
        text = builder.toString()
        parseMode = ParseMode.MARKDOWN2
    }

    fun silence(): SendMessageRequest = apply {
        disableNotification = true
    }

    fun protect(): SendMessageRequest = apply {
        protectContent = true
    }

    fun disablePreview(): SendMessageRequest = apply {
        disableWebPagePreview = false
    }

    fun reply(messageId: Long): SendMessageRequest = apply {
        replyToMessageId = messageId
        allowSendingWithoutReply = true
    }

    fun buildInlineKeyboard(block: InlineKeyboardMarkupBuilder.() -> Unit): SendMessageRequest = apply {
        val builder = InlineKeyboardMarkupBuilder()
        block(builder)
        keyboard = builder.build()
    }

    fun buildReplyKeyboard(block: ReplyKeyboardMarkupBuilder.() -> Unit): SendMessageRequest = apply {
        val builder = ReplyKeyboardMarkupBuilder()
        block(builder)
        keyboard = builder.build()
    }

    fun buildRemoveKeyboard(selective: Boolean = false): SendMessageRequest = apply {
        keyboard = ReplyKeyboardRemove(true, selective)
    }

    fun buildForceReply(): SendMessageRequest = apply {
        TODO("Not yet support")
    }
}
