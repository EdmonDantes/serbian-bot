package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import ru.loginov.telegram.api.entity.User

/**
 * Represents an invite link for a chat.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ChatInviteLink(
        /**
         * The invite link. If the link was created by another chat administrator,
         * then the second part of the link will be replaced with “…”.
         */
        @JsonProperty(value = "invite_link", required = true) val inviteLink: String,
        /**
         * Creator of the link
         */
        @JsonProperty(value = "creator", required = true) val creator: User,
        /**
         * True, if users joining the chat via the link need to be approved by chat administrators
         */
        @JsonProperty(value = "creates_join_request", required = true) val createsJoinRequest: Boolean,
        /**
         * True, if the link is primary
         */
        @JsonProperty(value = "is_primary", required = true) val isPrimary: Boolean,
        /**
         * True, if the link is revoked
         */
        @JsonProperty(value = "is_revoked", required = true) val isRevoked: Boolean,
        /**
         * Invite link name
         *
         * *Optional*
         */
        @JsonProperty(value = "name", required = false) val name: String?,
        /**
         * Point in time (Unix timestamp) when the link will expire or has been expired
         *
         * *Optional*
         */
        @JsonProperty(value = "expire_date", required = false) val expireDateTime: Long?,
        /**
         * Maximum number of users that can be members of the chat
         * simultaneously after joining the chat via this invite link; 1-99999
         *
         * *Optional*
         */
        @JsonProperty(value = "member_limit", required = false) val memberLimit: Long?,
        /**
         * Number of pending join requests created using this link
         *
         * *Optional*
         */
        @JsonProperty(value = "pending_join_request_count", required = false) val pendingJoinRequestCount: Long?,
)