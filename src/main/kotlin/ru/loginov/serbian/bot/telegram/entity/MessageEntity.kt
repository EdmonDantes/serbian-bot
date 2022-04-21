package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

/**
 * This object represents one special entity in a text message. For example, hashtags, usernames, URLs, etc.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageEntity(
        /**
         * Type of the entity.
         */
        @JsonProperty(value = "type", required = true) val type: MessageEntityType,
        /**
         * Offset in UTF-16 code units to the start of the entity
         */
        @JsonProperty(value = "offset", required = true) val offset: Long,
        /**
         * Length of the entity in UTF-16 code units
         */
        @JsonProperty(value = "length", required = true) val length: Long,
        /**
         * For [MessageEntityType.TEXT_LINK] only, url that will be opened after user taps on the text
         */
        @JsonProperty(value = "url", required = false) val url: String? = null,
        /**
         * For [MessageEntityType.TEXT_MENTIONS] only, the mentioned user
         */
        @JsonProperty(value = "user", required = false) val user: User? = null,
        /**
         * For [MessageEntityType.PRE] only, the programming language of the entity text
         */
        @JsonProperty(value = "language", required = false) val language: String? = null,
)

class MessageEntityBuilder {

    private var type: MessageEntityType? = null
    private var offset: Long? = null
    private var length: Long? = null
    private var url: String? = null
    private var user: User? = null
    private var language: String? = null

    fun mention(): MessageEntityBuilder = apply {
        type = MessageEntityType.MENTION
    }

    fun hashtag(): MessageEntityBuilder = apply {
        type = MessageEntityType.HASHTAG
    }

    fun cashtag(): MessageEntityBuilder = apply {
        type = MessageEntityType.CASHTAG
    }

    fun botCommand(): MessageEntityBuilder = apply {
        type = MessageEntityType.BOT_COMMAND
    }

    fun url(): MessageEntityBuilder = apply {
        type = MessageEntityType.URL
    }

    fun email(): MessageEntityBuilder = apply {
        type = MessageEntityType.EMAIL
    }

    fun phoneNumber(): MessageEntityBuilder = apply {
        type = MessageEntityType.PHONE_NUMBER
    }

    fun bold(): MessageEntityBuilder = apply {
        type = MessageEntityType.BOLD
    }

    fun italic(): MessageEntityBuilder = apply {
        type = MessageEntityType.ITALIC
    }

    fun underline(): MessageEntityBuilder = apply {
        type = MessageEntityType.UNDERLINE
    }

    fun strikethrough(): MessageEntityBuilder = apply {
        type = MessageEntityType.STRIKETHROUGH
    }

    fun spoiler(): MessageEntityBuilder = apply {
        type = MessageEntityType.SPOILER
    }

    fun code(): MessageEntityBuilder = apply {
        type = MessageEntityType.CODE
    }

    fun codeBlock(language: String? = null): MessageEntityBuilder = apply {
        type = MessageEntityType.PRE
        this.language = language
    }

    fun link(url: URL): MessageEntityBuilder = apply {
        this.type = MessageEntityType.TEXT_LINK
        this.url = url.toString()
    }

    fun textMention(user: User): MessageEntityBuilder = apply {
        this.type = MessageEntityType.TEXT_MENTIONS
        this.user = user
    }

    fun setStartPosition(startPosition: Number): MessageEntityBuilder = apply {
        offset = startPosition.toLong()
    }

    fun setEndPosition(endPosition: Number): MessageEntityBuilder = apply {
        length = endPosition.toLong() - (offset ?: error("Not yet set start position"))
    }

    fun setLength(length: Number): MessageEntityBuilder = apply {
        this.length = length.toLong()
    }

    fun build(): MessageEntity = MessageEntity(
            type ?: error("Builder isn't init. Doesn't set 'type' property"),
            offset ?: error("Builder isn't init. Doesn't set 'offset' property"),
            length ?: error("Builder isn't init. Doesn't set 'length' property"),
            url,
            user,
            language
    )
}

fun messageEntity(block: MessageEntityBuilder.() -> Unit) : MessageEntity {
    val builder = MessageEntityBuilder()
    block(builder)
    return builder.build()
}