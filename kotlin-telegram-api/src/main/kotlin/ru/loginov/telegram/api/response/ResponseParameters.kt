package ru.loginov.telegram.api.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Contains information about why a request was unsuccessful.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ResponseParameters(
        /**
         * *Optional*
         *
         * The group has been migrated to a supergroup with the specified identifier.
         */
        @JsonProperty(value = "migrate_to_chat_id", required = false) val migrateToChatId: Int?,
        /**
         * *Optional*
         *
         * In case of exceeding flood control, the number of seconds left to wait before the request can be repeated
         */
        @JsonProperty(value = "retry_after", required = false) val replyAfter: Int?,
)