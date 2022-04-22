package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import ru.loginov.telegram.api.entity.InlineKeyboardButton

/**
 * This object represents an inline keyboard that appears right next to the message it belongs to.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class InlineKeyboardMarkup(
        /**
         * Array of button rows, each represented by an Array of InlineKeyboardButton objects
         */
        @JsonProperty(value = "inline_keyboard", required = true) val keyboardButtons: List<List<InlineKeyboardButton>>
)