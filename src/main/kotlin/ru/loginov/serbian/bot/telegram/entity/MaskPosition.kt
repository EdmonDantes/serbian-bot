package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object describes the position on faces where a mask should be placed by default.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MaskPosition(
        /**
         * The part of the face relative to which the mask should be placed.
         * One of “forehead”, “eyes”, “mouth”, or “chin”.
         */
        @JsonProperty(value = "point", required = true) val point: String,
        /**
         * Shift by X-axis measured in widths of the mask scaled to the face size,
         * from left to right. For example, choosing -1.0 will place mask just
         * to the left of the default mask position.
         */
        @JsonProperty(value = "x_shift", required = true) val xShift: Double,
        /**
         * Shift by Y-axis measured in heights of the mask scaled to the face size,
         * from top to bottom. For example, 1.0 will place the mask just below
         * the default mask position.
         */
        @JsonProperty(value = "y_shift", required = true) val yShift: Double,
        /**
         * Mask scaling coefficient. For example, 2.0 means double size.
         */
        @JsonProperty(value = "scale", required = true) val scale: Double,
)