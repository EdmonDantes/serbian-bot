package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object contains information about one answer option in a poll.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PollOption(
        /**
         * Option text, 1-100 characters
         */
        @JsonProperty(value = "text", required = true) val text: String,
        /**
         * Number of users that voted for this option
         */
        @JsonProperty(value = "voter_count", required = true) val voterCount: Long
)