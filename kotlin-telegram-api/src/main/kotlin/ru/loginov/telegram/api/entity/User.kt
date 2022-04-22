package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents a Telegram user or bot.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
        /**
         * Unique identifier for this user or bot.
         */
        @JsonProperty(value = "id", required = true) val id: Long,
        /**
         * True, if this user is a bot
         */
        @JsonProperty(value = "is_bot", required = true) val isBot: Boolean,
        /**
         * User's or bots first name
         */
        @JsonProperty(value = "first_name", required = true) val firstName: String,
        /**
         * User's or bots last name
         *
         * *Optional*
         */
        @JsonProperty(value = "last_name", required = false) val lastName: String?,
        /**
         * User's or bots username
         *
         * *Optional*
         */
        @JsonProperty(value = "username", required = false) val username: String?,
        /**
         * IETF language tag of the user's language
         *
         * *Optional*
         */
        @JsonProperty(value = "language_code", required = false) val languageTag: String?,
        /**
         * True, if the bot can be invited to group.
         *
         * Returned only in *getMe*.
         *
         * *Optional*
         */
        @JsonProperty(value = "can_join_groups", required = false) val canJoinGroups: Boolean?,
        /**
         * True, if privacy mode is disabled for the bot.
         *
         * Returned only in *getMe*.
         *
         * *Optional*
         */
        @JsonProperty(value = "can_read_all_group_messages", required = false) val canReadAllGroupsMessage: Boolean?,
        /**
         * True, if the bot supports inline queries.
         *
         * Returned only in *getMe*.
         *
         * *Optional*
         */
        @JsonProperty(value = "supports_inline_queries", required = false) val supportInlineQueries: Boolean?
)