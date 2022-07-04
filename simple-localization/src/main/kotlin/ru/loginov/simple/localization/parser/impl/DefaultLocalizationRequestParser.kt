package ru.loginov.simple.localization.parser.impl

import ru.loginov.simple.localization.LocalizationKey
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.exception.NotEscapedCharacterException
import ru.loginov.simple.localization.impl.DefaultLocalizationKey
import ru.loginov.simple.localization.impl.DefaultLocalizationRequest
import ru.loginov.simple.localization.parser.LocalizationRequestParser

class DefaultLocalizationRequestParser : LocalizationRequestParser {
    override fun parse(str: String): LocalizationRequest {
        val keys = ArrayList<LocalizationKey>()

        var prevLastIndex: Int = -1
        var match = STRING_FOR_LOCALIZATION.find(str)

        while (match != null) {
            if (prevLastIndex + 1 < match.range.first) {
                val middle = str.substring(prevLastIndex + 1, match.range.first)
                keys.add(
                        DefaultLocalizationKey(
                                "",
                                unescapeCharacters(
                                        middle, middle, str, prevLastIndex + 1 until match.range.first
                                )
                        )
                )
            }

            val foundStr = match.value
            val range = match.range

            prevLastIndex = match.range.last
            match = match.next()

            if (foundStr.isEmpty()) {
                continue
            }

            if (foundStr[0] == '\\') {
                keys.add(
                        DefaultLocalizationKey(
                                "",
                                unescapeCharacters(foundStr.substring(1), foundStr, str, range)
                        )
                )
                continue
            }

            val forLocalizationDefinition = splitArguments(foundStr, 1)
            if (forLocalizationDefinition.isEmpty()) {
                error("Can not find localization key for match string '$foundStr' in indexes '$range'")
            }
            val localizationKey = forLocalizationDefinition[0]
            if (localizationKey.isEmpty()) {
                error("Localization key can not be empty. Match string '$foundStr' in indexes '$range'")
            }

            keys.add(
                    DefaultLocalizationKey(
                            unescapeCharacters(localizationKey, foundStr, str, range),
                            forLocalizationDefinition.drop(1).map { unescapeCharacters(it, foundStr, str, range) }
                    )
            )
        }

        if (prevLastIndex + 1 < str.length) {
            val last = str.substring(prevLastIndex + 1)
            keys.add(
                    DefaultLocalizationKey(
                            "",
                            unescapeCharacters(last, last, str, prevLastIndex + 1 until str.length)
                    )
            )
        }

        return DefaultLocalizationRequest(keys)
    }

    private fun unescapeCharacters(str: String, allStr: String, foundStr: String, range: IntRange): String {
        val match = NOT_ESCAPED_CHARACTERS.find(str)
        if (match != null) {
            throw NotEscapedCharacterException(
                    allStr,
                    foundStr,
                    range,
                    match.range.first + range.first..match.range.last + range.first
            )
        }

        return str.replace(ESCAPED_CHARACTERS) { it.value.substring(1) }
    }

    private fun splitArguments(str: String, startIndex: Int = 0): List<String> {
        return ARGUMENTS_FOR_STRING_FOR_LOCALIZATION.findAll(str, startIndex)
                .map { it.value }
                .filter { it.length > 2 && !it.startsWith('\\') }
                .map { it.substring(1, it.length - 1) }
                .toList()
    }

    companion object {
        private val STRING_FOR_LOCALIZATION = Regex("(\\\\)?@((\\{\\})|(\\{(.*?)[^\\\\]\\}))+")
        private val ARGUMENTS_FOR_STRING_FOR_LOCALIZATION = Regex("((\\{\\})|(\\{(.*?)[^\\\\]\\}))")
        private val ESCAPED_CHARACTERS = Regex("\\\\[@{}]")
        private val NOT_ESCAPED_CHARACTERS = Regex("(?<!\\\\)[@{}]")
    }
}