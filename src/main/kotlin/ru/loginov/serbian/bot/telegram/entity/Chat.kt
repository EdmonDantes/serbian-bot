package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents a chat.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Chat(
        /**
         * Unique identifier for this chat.
         */
        @JsonProperty(value = "id", required = true) val id: Long,
        /**
         * Type of chat, can be either “private”, “group”, “supergroup” or “channel”
         */
        @JsonProperty(value = "type", required = true) val type: String,
        /**
         * Title, for supergroups, channels and group chats
         *
         * *Optional*
         */
        @JsonProperty(value = "title", required = false) val title: String?,
        /**
         * Username, for private chats, supergroups and channels if available
         *
         * *Optional*
         */
        @JsonProperty(value = "username", required = false) val username: String?,
        /**
         * First name of the other party in a private chat
         *
         * *Optional*
         */
        @JsonProperty(value = "first_name", required = false) val firstName: String,
        /**
         * Last name of the other party in a private chat
         *
         * *Optional*
         */
        @JsonProperty(value = "last_name", required = false) val lastName: String?,
        /**
         * Chat photo.
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "photo", required = false) val photo: ChatPhoto?,
        /**
         * Bio of the other party in a private chat.
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "bio", required = false) val bio: String?,
        /**
         * True, if privacy settings of the other party in the private chat allows
         * to use *tg://user?id=<user_id>* links only in chats with the user.
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "has_private_forwards", required = false) val hasPrivateForwards: Boolean?,
        /**
         * Description, for groups, supergroups and channel chats.
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "description", required = false) val description: String?,
        /**
         * Primary invite link, for groups, supergroups and channel chats.
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "invite_link", required = false) val inviteLink: String?,
        /**
         * The most recent pinned message (by sending date)
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "pinned_message", required = false) val pinnedMessage: Message?,
        /**
         * Default chat member permissions, for groups and supergroups
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "permissions", required = false) val permissions: ChatPermissions?,
        /**
         * For supergroups, the minimum allowed delay between consecutive messages
         * sent by each unpriviledged user; in seconds.
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "show_mode_delay", required = false) val showModeDelaySeconds: Long?,
        /**
         * The time after which all messages sent to the chat will be automatically deleted; in seconds.
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "message_auto_delete_time", required = false) val messageAutoDeleteTimeoutSeconds: Long?,
        /**
         * True, if messages from the chat can't be forwarded to other chats.
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "has_protected_content", required = false) val hasProtectedContent: Boolean?,
        /**
         * For supergroups, name of group sticker set.
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "sticker_set_name", required = false) val stickerSetName: String?,
        /**
         * True, if the bot can change the group sticker set.
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "can_set_sticker_set", required = false) val canSetStickerSet: Boolean?,
        /**
         * Unique identifier for the linked chat, i.e. the discussion group identifier
         * for a channel and vice versa; for supergroups and channel chats.
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "linked_chat_id", required = false) val linkedChatId: Long?,
        /**
         * For supergroups, the location to which the supergroup is connected.
         *
         * *Optional*
         *
         * It is returned only for ***getChat*** method
         */
        @JsonProperty(value = "location", required = false) val location: ChatLocation?
)