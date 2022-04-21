package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

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

class InlineKeyboardMarkupBuilder : AbstractKeyboardBuilder<InlineKeyboardButton>() {

    fun add(button: InlineKeyboardButton) : InlineKeyboardMarkupBuilder = apply {
        addButton(button)
    }

    fun rect(width: Number, height: Number): InlineKeyboardMarkupBuilder = apply {
        setRect(width, height)
    }

    fun width(width: Number): InlineKeyboardMarkupBuilder = apply {
        setWidth(width)
    }

    fun height(height: Number) : InlineKeyboardMarkupBuilder = apply {
        setHeight(height)
    }

    fun build() : InlineKeyboardMarkup = InlineKeyboardMarkup(keyboardButtons)
}