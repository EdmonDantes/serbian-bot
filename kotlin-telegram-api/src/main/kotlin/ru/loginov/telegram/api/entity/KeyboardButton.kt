package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents one button of the reply keyboard.
 * For simple text buttons String can be used instead of this object to specify text of the button.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class KeyboardButton(
        /**
         * Text of the button. If none of the optional fields are used,
         * it will be sent as a message when the button is pressed
         */
        @JsonProperty(value = "text", required = true) val text: String,
        /**
         * If True, the user's phone number will be sent as a contact when the button is pressed.
         * Available in private chats only.
         *
         * *Optional*
         */
        @JsonProperty(value = "request_contact", required = false) val requiredContact: Boolean? = null,
        /**
         * If True, the user's current location will be sent when the button is pressed.
         * Available in private chats only.
         *
         * *Optional*
         */
        @JsonProperty(value = "request_location", required = false) val requestLocation: Boolean? = null,
//        @JsonProperty(value = "request_poll", required = false) val request_poll: KeyboardButtonPollType?,
//        @JsonProperty(value = "web_app", required = false) val web_app: WebAppInfo?,
)