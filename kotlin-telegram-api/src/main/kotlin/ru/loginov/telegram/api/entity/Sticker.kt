package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents a sticker.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Sticker(
        /**
         * Identifier for this file, which can be used to download or reuse the file
         */
        @JsonProperty(value = "file_id", required = true) val file_id: String,
        /**
         * Unique identifier for this file, which is supposed
         * to be the same over time and for different bots.
         * Can't be used to download or reuse the file.
         */
        @JsonProperty(value = "file_unique_id", required = true) val file_unique_id: String,
        /**
         * Sticker width
         */
        @JsonProperty(value = "width", required = true) val width: Long,
        /**
         * Sticker height
         */
        @JsonProperty(value = "height", required = true) val height: Long,
        /**
         * True, if the sticker is animated
         */
        @JsonProperty(value = "is_animated", required = true) val isAnimated: Boolean,
        /**
         * True, if the sticker is a video sticker
         */
        @JsonProperty(value = "is_video", required = true) val isVideo: Boolean,
        /**
         * Thumbnail of the album cover to which the music file belongs
         *
         * *Optional*
         */
        @JsonProperty(value = "thumb", required = false) val thumb: PhotoSize?,
        /**
         * Emoji associated with the sticker
         *
         * *Optional*
         */
        @JsonProperty(value = "emoji", required = false) val emoji: String?,
        /**
         * Name of the sticker set to which the sticker belongs
         *
         * *Optional*
         */
        @JsonProperty(value = "set_name", required = false) val setName: String?,
        /**
         * For mask stickers, the position where the mask should be placed
         *
         * *Optional*
         */
        @JsonProperty(value = "mask_position", required = false) val maskPosition: MaskPosition?,
        /**
         * File size in bytes
         *
         * *Optional*
         */
        @JsonProperty(value = "file_size", required = false) val sizeBytes: Long?,
)