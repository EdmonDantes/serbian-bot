package ru.loginov.serbian.bot.telegram.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents a chat photo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ChatPhoto(
        /**
         * File identifier of small (160x160) chat photo.
         * This file_id can be used only for photo download
         * and only for as long as the photo is not changed.
         */
        @JsonProperty(value = "small_file_id", required = true) val smallFileId: Long,
        /**
         * Unique file identifier of small (160x160) chat photo,
         * which is supposed to be the same over time and for different bots.
         * Can't be used to download or reuse the file.
         */
        @JsonProperty(value = "small_file_unique_id", required = true) val smallFileUniqueId: Long,
        /**
         * File identifier of big (640x640) chat photo.
         * This file_id can be used only for photo download
         * and only for as long as the photo is not changed.
         */
        @JsonProperty(value = "big_file_id", required = true) val bigFileId: Long,
        /**
         * Unique file identifier of big (640x640) chat photo, which is supposed
         * to be the same over time and for different bots.
         * Can't be used to download or reuse the file.
         */
        @JsonProperty(value = "big_file_unique_id", required = true) val bigFileUniqueId: Long,
)