package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents a point on the map.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Location(
        /**
         * Longitude as defined by sender
         */
        @JsonProperty(value = "longitude", required = true) val longitude: Double,
        /**
         * Latitude as defined by sender
         */
        @JsonProperty(value = "latitude", required = true) val latitude: Double,
        /**
         * The radius of uncertainty for the location, measured in meters; 0-1500
         *
         * *Optional*
         */
        @JsonProperty(value = "horizontal_accuracy", required = false) val horizontalAccuracy: Double,
        /**
         * Time relative to the message sending date, during which the location can be updated; in seconds.
         * For active live locations only.
         *
         * *Optional*
         */
        @JsonProperty(value = "live_period", required = false) val livePeriod: Long,
        /**
         * The direction in which user is moving, in degrees; 1-360.
         * For active live locations only.
         *
         * *Optional*
         */
        @JsonProperty(value = "heading", required = false) val heading: Long,
        /**
         * Maximum distance for proximity alerts about approaching another chat member, in meters.
         * For sent live locations only.
         *
         * *Optional*
         */
        @JsonProperty(value = "proximity_alert_radius", required = false) val proximityAlertRadius: Long,
)