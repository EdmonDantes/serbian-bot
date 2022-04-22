package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ru.loginov.telegram.api.entity.User

/**
 * This object represents an answer of a user in a non-anonymous poll.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PollAnswer(
        @JsonProperty(value = "poll_id", required = true) val pollId: String,
        @JsonProperty(value = "user", required = true) val user: User,
        @JsonProperty(value = "options_ids", required = true) val optionsIds: List<Long>
)