package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a location to which a chat is connected.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ChatLocation(
        /**
         * The location to which the supergroup is connected. Can't be a live location.
         */
        @JsonProperty(value = "location", required = true) val location: Location,
        /**
         * Location address; 1-64 characters, as defined by the chat owner
         */
        @JsonProperty(value = "address", required = true) val address: String
)