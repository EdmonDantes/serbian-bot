package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents one size of a photo or a file / sticker thumbnail.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PhotoSize(
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
         * Photo width
         */
        @JsonProperty(value = "width", required = true) val width: Long,
        /**
         * Photo height
         */
        @JsonProperty(value = "height", required = true) val height: Long,
        /**
         * File size in bytes
         *
         * *Optional*
         */
        @JsonProperty(value = "file_size", required = false) val sizeBytes: Long?,
)