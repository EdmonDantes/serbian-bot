package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents an animation file (GIF or H.264/MPEG-4 AVC video without sound).
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Animation(
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
         * Video width as defined by sender
         */
        @JsonProperty(value = "width", required = true) val width: Long,
        /**
         * Video height as defined by sender
         */
        @JsonProperty(value = "height", required = true) val height: Long,
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
         * Original animation filename as defined by sender
         *
         * *Optional*
         */
        @JsonProperty(value = "file_name", required = false) val name: String?,
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