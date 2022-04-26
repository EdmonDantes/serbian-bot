package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import ru.loginov.telegram.api.entity.User

/**
 * This object contains information about one member of a chat.
 */
//TODO: Add support 6 types for this object
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ChatMember(
        /**
         * The member's status in the chat
         */
        @JsonProperty(value = "status", required = true) val status: String, // TODO: Use enum
        /**
         * Information about the user
         */
        @JsonProperty(value = "user", required = true) val user: User,
)