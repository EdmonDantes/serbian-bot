package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents an audio file to be treated as music by the Telegram clients.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Audio(
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
         * Duration of the audio in seconds as defined by sender
         */
        @JsonProperty(value = "duration", required = true) val durationSec: Long,
        /**
         * Performer of the audio as defined by sender or by audio tags
         *
         * *Optional*
         */
        @JsonProperty(value = "performer", required = false) val performer: String?,
        /**
         * Title of the audio as defined by sender or by audio tags
         *
         * *Optional*
         */
        @JsonProperty(value = "title", required = false) val title: String?,
        /**
         * Original filename as defined by sender
         *
         * *Optional*
         */
        @JsonProperty(value = "file_name", required = false) val fileName: String?,
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
        /**
         * Thumbnail of the album cover to which the music file belongs
         *
         * *Optional*
         */
        @JsonProperty(value = "thumb", required = false) val thumb: PhotoSize?,
)