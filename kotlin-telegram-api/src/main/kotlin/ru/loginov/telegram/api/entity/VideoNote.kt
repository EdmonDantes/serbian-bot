package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents a video message (available in Telegram apps as of v.4.0).
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class VideoNote(
        /**
         * Identifier for this file, which can be used to download or reuse the file
         */
        @JsonProperty(value = "file_id", required = true) val fileId: String,
        /**
         * Unique identifier for this file, which is supposed to be the same over time
         * and for different bots. Can't be used to download or reuse the file.
         */
        @JsonProperty(value = "file_unique_id", required = true) val fileUniqueId: String,
        /**
         * Video width and height (diameter of the video message) as defined by sender
         */
        @JsonProperty(value = "length", required = true) val length: Long,
        /**
         * Duration of the video in seconds as defined by sender
         */
        @JsonProperty(value = "duration", required = true) val durationSec: Long,
        /**
         * Animation thumbnail as defined by sender
         *
         * *Optional*
         */
        @JsonProperty(value = "thumb", required = false) val thumb: PhotoSize?,
        /**
         * File size in bytes
         *
         * *Optional*
         */
        @JsonProperty(value = "file_size", required = false) val sizeBytes: Long?,
)