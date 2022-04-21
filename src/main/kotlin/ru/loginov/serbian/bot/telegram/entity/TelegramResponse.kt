package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TelegramResponse<T> @JsonCreator constructor(
        @JsonProperty("ok") val isOk: Boolean,
        @JsonProperty("description") val description: String?,
        @JsonProperty("error_code") val errorCode: Int?,
        @JsonProperty("parameters") val parameters: Void?,
        @JsonProperty("result") val result: T?
)