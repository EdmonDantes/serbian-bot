package ru.loginov.simple.localization.parser

import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.exception.NotEscapedCharacterException

interface LocalizationRequestParser {

    /**
     * Parse [str] to [LocalizationRequest]
     *
     * Format:
     * `not_localize@{key.for.localization}{arg1}{arg2}not_localize`
     *
     * Please use \ before characters '{','}','@' if you want to add they to result
     *
     * @throws NotEscapedCharacterException if fount not escaped character
     * @throws IllegalStateException if didn't find localization key or it is empty
     */
    @Throws(NotEscapedCharacterException::class)
    fun parse(str: String): LocalizationRequest

}