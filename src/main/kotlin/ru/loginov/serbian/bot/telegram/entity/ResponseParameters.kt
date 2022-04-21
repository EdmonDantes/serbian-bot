package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseParameters(
        @JsonProperty("migrate_to_chat_id") val migrateToChatId: Int?,
        @JsonProperty("retry_after") val replyAfter: Int?,
)