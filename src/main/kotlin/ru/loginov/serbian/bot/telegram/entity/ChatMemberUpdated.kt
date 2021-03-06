package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents changes in the status of a chat member.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ChatMemberUpdated(
        /**
         * Chat the user belongs to
         */
        @JsonProperty(value = "chat", required = true) val chat: Chat,
        /**
         * Performer of the action, which resulted in the change
         */
        @JsonProperty(value = "from", required = true) val from: User,
        /**
         * Date the change was done in Unix time
         */
        @JsonProperty(value = "date", required = true) val dateTime: Long,
        /**
         * Previous information about the chat member
         */
        @JsonProperty(value = "old_chat_member", required = true) val oldChatMember: ChatMember,
        /**
         * New information about the chat member
         */
        @JsonProperty(value = "new_chat_member", required = true) val newChatMember: ChatMember,
        /**
         * Chat invite link, which was used by the user to join the chat; for joining by invite link events only.
         *
         * *Optional*
         */
        @JsonProperty(value = "invite_link", required = false) val inviteLink: ChatInviteLink?,
)