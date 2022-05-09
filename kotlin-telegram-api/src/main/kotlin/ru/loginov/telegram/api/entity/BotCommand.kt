package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents a bot command.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class BotCommand(
        /**
         * Text of the command; 1-32 characters.
         * Can contain only lowercase English letters, digits and underscores.
         */
        @JsonProperty(value = "command", required = true) val command: String,
        /**
         * Description of the command; 1-256 characters.
         */
        @JsonProperty(value = "description", required = true) val description: String,
)