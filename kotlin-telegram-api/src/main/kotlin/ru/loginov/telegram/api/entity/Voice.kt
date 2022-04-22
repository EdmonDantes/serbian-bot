package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents a voice note.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Voice(
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
         * Duration of the video in seconds as defined by sender
         */
        @JsonProperty(value = "duration", required = true) val durationSec: Long,
        /**
         * MIME type of the file as defined by sender
         *
         * *Optional*
         */
        @JsonProperty(value = "mime_type", required = false) val mimeType: String?,
        /**
         * File size in bytes
         *
         * *Optional*
         */
        @JsonProperty(value = "file_size", required = false) val sizeBytes: Long?,
)