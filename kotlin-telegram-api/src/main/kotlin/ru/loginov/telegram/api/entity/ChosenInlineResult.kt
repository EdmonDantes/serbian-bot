package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a result of an inline query that was chosen by the user and sent to their chat partner.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ChosenInlineResult(
        /**
         * The unique identifier for the result that was chosen
         */
        @JsonProperty(value = "result_id", required = true) val id: String,
        /**
         * The user that chose the result
         */
        @JsonProperty(value = "from", required = true) val from: User,
        /**
         * Sender location, only for bots that require user location
         *
         * *Optional*
         */
        @JsonProperty(value = "location", required = false) val location: Location?,
        /**
         * Identifier of the sent inline message. Available only if there is an inline keyboard attached to the message.
         * Will be also received in callback queries and can be used to edit the message.
         *
         * *Optional*
         */
        @JsonProperty(value = "inline_message_id", required = false) val inlineMessageId: String?,
        /**
         * The query that was used to obtain the result
         */
        @JsonProperty(value = "query", required = true) val query: String,
)