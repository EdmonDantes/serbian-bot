package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents an incoming inline query.
 * When the user sends an empty query, your bot could return some default or trending results.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class InlineQuery(
        /**
         * Unique identifier for this query
         */
        @JsonProperty(value = "message", required = true) val id: String,
        /**
         * Sender
         */
        @JsonProperty(value = "from", required = true) val from: User,
        /**
         * Text of the query (up to 256 characters)
         */
        @JsonProperty(value = "query", required = true) val query: String,
        /**
         * Offset of the results to be returned, can be controlled by the bot
         */
        @JsonProperty(value = "offset", required = true) val offset: String,
        /**
         * Type of the chat, from which the inline query was sent.
         * Can be either “sender” for a private chat with the inline query sender, “private”, “group”, “supergroup”, or “channel”.
         * The chat type should be always known for requests sent from official clients and most third-party clients,
         * unless the request was sent from a secret chat
         *
         * *Optional*
         */
        @JsonProperty(value = "chat_type", required = false) val chatType: String?,
        /**
         * Sender location, only for bots that request user location
         *
         * *Optional*
         */
        @JsonProperty(value = "location", required = false) val location: Location?,
)