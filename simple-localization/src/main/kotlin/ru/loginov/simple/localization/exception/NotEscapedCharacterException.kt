package ru.loginov.simple.localization.exception

class NotEscapedCharacterException(
        /**
         * String which constance not escaped character
         */
        val strForParsing: String,
        /**
         * Part which scanned for not escaped characters
         */
        val processingPart: String,
        /**
         * Indexes of [processingPart] in [strForParsing]
         */
        val processingPartRange: IntRange,
        /**
         * Indexes of not escaped characters
         */
        val notEscapedRange: IntRange
) : RuntimeException(
        "Found not escaped characters. " +
                "Please check you string. Match string '$processingPart' in indexes '$processingPartRange'. " +
                "Not escaped characters on '$notEscapedRange'"
) {
}