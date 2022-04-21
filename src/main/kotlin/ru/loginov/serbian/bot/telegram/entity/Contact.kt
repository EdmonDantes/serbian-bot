package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents a phone contact.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Contact(
        /**
         * Contact's phone number
         */
        @JsonProperty(value = "phone_number", required = true) val phoneNumber: String,
        /**
         * Contact's first name
         */
        @JsonProperty(value = "first_name", required = true) val firstName: String,
        /**
         * Contact's last name
         *
         * *Optional*
         */
        @JsonProperty(value = "last_name", required = false) val last_name: String?,
        /**
         * Contact's user identifier in Telegram.
         *
         * *Optional*
         */
        @JsonProperty(value = "user_id", required = false) val userId: Long?,
        /**
         * Additional data about the contact in the form of a *vCard*
         *
         * *Optional*
         */
        @JsonProperty(value = "vcard", required = false) val vcard: String?,
)