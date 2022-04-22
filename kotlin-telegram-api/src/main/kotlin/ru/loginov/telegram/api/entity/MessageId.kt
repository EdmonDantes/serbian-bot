package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents one special entity in a text message. For example, hashtags, usernames, URLs, etc.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageId(
        /**
         * Unique message identifier
         */
        @JsonProperty(value = "message_id", required = true) val id: Long
)