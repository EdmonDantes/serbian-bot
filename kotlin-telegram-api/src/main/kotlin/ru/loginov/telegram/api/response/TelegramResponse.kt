package ru.loginov.telegram.api.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents a json response from telegram api
 *
 * If [isSuccess] equals True, the request was successful and the result of the query can be found
 * in the [result] field. In case of an unsuccessful request, [isSuccess] equals false
 * and the error is explained in the [description].
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class TelegramResponse<T> @JsonCreator constructor(
        /**
         * If true, request finished success, else one failed
         */
        @JsonProperty(value = "ok", required = true) val isSuccess: Boolean,
        /**
         * *Optional*
         *
         * Human-readable description of the result
         */
        @JsonProperty(value = "description", required = false) val description: String?,
        /**
         * _**Deprecated**_ *Optional*
         *
         * Error code
         */
        @Deprecated(
                message = "It's contents are subject to change in the future",
                replaceWith = ReplaceWith("description"),
                level = DeprecationLevel.WARNING
        )
        @JsonProperty(value = "error_code", required = false) val errorCode: Int?,
        /**
         * *Optional*
         *
         * Some errors may also have this field, which can help to automatically handle the error.
         */
        @JsonProperty(value = "parameters", required = false) val parameters: ResponseParameters?,
        /**
         * *Optional*
         */
        @JsonProperty(value = "result", required = false) val result: T?
)