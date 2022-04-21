package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents an animated emoji that displays a random value.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Dice(
        @JsonProperty(value = "emoji", required = true) val emoji: String,
        @JsonProperty(value = "value", required = true) val value: Long,
)