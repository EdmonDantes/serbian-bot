package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents an answer of a user in a non-anonymous poll.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PollAnswer(
        @JsonProperty(value = "poll_id", required = true) val pollId: String,
        @JsonProperty(value = "user", required = true) val user: User,
        @JsonProperty(value = "options_ids", required = true) val optionsIds: List<Long>
)