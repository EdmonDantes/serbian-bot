package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a join request sent to a chat.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ChatJoinRequest(
        /**
         * Chat to which the request was sent
         */
        @JsonProperty(value = "chat", required = true) val chat: Chat,
        /**
         * User that sent the join request
         */
        @JsonProperty(value = "from", required = true) val from: User,
        /**
         * Date the request was sent in Unix time
         */
        @JsonProperty(value = "date", required = true) val date: Long,
        /**
         * Bio of the user.
         *
         * *Optional*
         */
        @JsonProperty(value = "bio", required = false) val bio: String?,
        /**
         * Chat invite link that was used by the user to send the join request
         *
         * *Optional*
         */
        @JsonProperty(value = "invite_link", required = false) val inviteLink: ChatInviteLink?,
)