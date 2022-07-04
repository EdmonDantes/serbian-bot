package ru.loginov.simple.localization.manager.impl

import ru.loginov.simple.localization.LocalizationKey
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext
import ru.loginov.simple.localization.context.impl.DefaultLocalizationContext
import ru.loginov.simple.localization.exception.LanguageNotSupportedException
import ru.loginov.simple.localization.exception.NotEscapedCharacterException
import ru.loginov.simple.localization.manager.LocalizationManager

abstract class AbstractLocalizationManager(
        final override val supportLanguages: List<String>
) : LocalizationManager {

    private val localizationContexts: Map<String, LocalizationContext>

    init {
        localizationContexts = supportLanguages
                .associateWith { DefaultLocalizationContext(it, this) }

    }

    override fun isSupport(language: String): Boolean = language == null || supportLanguages.contains(language)

    override fun localize(language: String?, key: LocalizationKey): String =
            localizeOrNull(language, key) ?: key.key

    override fun localizeOrNull(language: String?, key: LocalizationKey): String? {
        if (language != null && !isSupport(language)) {
            throw LanguageNotSupportedException(language)
        }

        return if (key.key.isBlank()) {
            null
        } else {
            findByKey(language ?: defaultLanguage, key.key)?.let { localized ->
                if (key.arguments.isEmpty())
                    unescapeCharacters(localized)
                else
                    unescapeCharacters(processArguments(localized, key.arguments))
            }
        }
    }

    override fun localizeOrDefault(language: String?, key: LocalizationKey, default: String): String {
        if (language != null && !isSupport(language)) {
            throw LanguageNotSupportedException(language)
        }

        return if (key.key.isBlank()) {
            default
        } else {
            (findByKey(language ?: defaultLanguage, key.key) ?: default).let { localized ->
                if (key.arguments.isEmpty())
                    unescapeCharacters(localized)
                else
                    unescapeCharacters(processArguments(localized, key.arguments))
            }
        }
    }

    override fun localize(language: String?, request: LocalizationRequest): String {
        if (language != null && !isSupport(language)) {
            throw LanguageNotSupportedException(language)
        }

        val resultBuilder = StringBuilder()
        request.keys.forEach {
            resultBuilder.append(localize(language, it))
        }
        return resultBuilder.toString()
    }

    override fun getContext(language: String?): LocalizationContext {
        if (language != null && !isSupport(language)) {
            throw LanguageNotSupportedException(language)
        }

        return localizationContexts[language]
                ?: error("Can not find LocalizationContext for language '$language'")
    }

    protected fun processArguments(localized: String, arguments: List<String>): String {
        var index = 0
        return localized.replace(ARGUMENT_IN_LOCALIZATION) {
            val argumentIndex = it.groupValues[2].toIntOrNull() ?: index++
            if (argumentIndex < arguments.size) {
                arguments[argumentIndex]
            } else {
                "\\{?\\}"
            }
        }
    }

    private fun unescapeCharacters(str: String): String {
        val match = NOT_ESCAPED_CHARACTERS.find(str)
        if (match != null) {
            throw NotEscapedCharacterException(
                    str,
                    str,
                    str.indices,
                    match.range
            )
        }

        return str.replace(ESCAPED_CHARACTERS) { it.value.substring(1) }
    }

    protected abstract fun findByKey(language: String, key: String): String?

    companion object {
        private val ARGUMENT_IN_LOCALIZATION = Regex("(?<!\\\\)(\\{([0-9]*)\\})")
        private val ESCAPED_CHARACTERS = Regex("\\\\[\\{\\}]")
        private val NOT_ESCAPED_CHARACTERS = Regex("(?<!\\\\)[\\{\\}]")
    }
}
