package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

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