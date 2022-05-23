package ru.loginov.serbian.bot.data.manager.localization

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.manager.localization.exception.LanguageNotSupportedException
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle
import kotlin.streams.toList

//TODO: Will move data to database and add integration with database
@Service
class DefaultLocalizationManager(
        /**
         * Which languages is supported by application
         */
        @Value("\${bot.language.support:en;ru}") supportLanguages: String,
        /**
         * Default language
         */
        @Value("\${bot.language.default:en}") defaultLanguage: String,
        /**
         * Base name for resources bundle for localization
         */
        @Value("\${bot.language.file.base.name:}") private val baseNameForLocalizationFiles: String
) : LocalizationManager {

    final override val supportLanguages: List<String>
    final override val defaultLanguage: String
    private val localeBundle: MutableMap<String, ResourceBundle> = HashMap()

    init {
        this.supportLanguages = supportLanguages.split(';').map { it.lowercase() }
        this.defaultLanguage = defaultLanguage

        this.supportLanguages.forEach { lang ->
            loadLocalizationResourceBundle(lang)?.also {
                localeBundle[lang] = it
            }
        }
    }

    override fun isSupport(language: String): Boolean = language.contains(language.lowercase())

    override fun findLocalizedStringByKey(language: String?, key: String): String? {
        if (language != null && !isSupport(language)) {
            throw LanguageNotSupportedException(language)
        }
        return try {
            getLocalizationResourceBundle(language ?: defaultLanguage)?.getString(key)
        } catch (e: MissingResourceException) {
            LOGGER.debug("Can not find localized string for key '$key' for language '$language'", e)
            null
        }
    }

    override fun transformStringToLocalized(language: String?, str: String): String {
        if (language != null && !isSupport(language)) {
            throw LanguageNotSupportedException(language)
        }

        return str?.replace(STRING_FOR_LOCALIZATION) { match ->
            val str = match.value
            if (str.isEmpty()) {
                str
            } else if (str[0] == '\\') {
                str.substring(1)
            } else {
                val forLocalizationDefinition = splitArguments(str, 1)
                if (forLocalizationDefinition.isEmpty()) {
                    str
                }
                val localizationKey = forLocalizationDefinition[0]

                val arguments = forLocalizationDefinition.stream().skip(1).map { it }.toList()
                findLocalizedStringByKey(language, localizationKey)?.let {
                    var index = 0
                    it.replace(ARGUMENTS_FOR_STRING_FOR_LOCALIZATION) { matchForArg ->
                        val str = matchForArg.value
                        when {
                            str.isEmpty() -> str
                            str.startsWith('\\') -> str.substring(1)
                            else -> {
                                val i = str.substring(1, str.length - 1).toIntOrNull() ?: index++
                                arguments[i]
                            }
                        }
                    }
                }
                        ?: localizationKey
            }
        }
    }

    private fun getLocalizationResourceBundle(language: String): ResourceBundle? =
            if (supportLanguages.contains(language.lowercase())) {
                localeBundle[language]
            } else {
                null
            }


    private fun loadLocalizationResourceBundle(language: String): ResourceBundle? =
            try {
                ResourceBundle.getBundle(baseNameForLocalizationFiles, Locale.forLanguageTag(language))
            } catch (e: Exception) {
                LOGGER.error("Can not load resource bundle with base name '$baseNameForLocalizationFiles' and language '$language'")
                null
            }


    private fun splitArguments(str: String, startIndex: Int = 0): List<String> {
        val result = ArrayList<String>()

        var match = ARGUMENTS_FOR_STRING_FOR_LOCALIZATION.find(str, startIndex)
        while (match != null) {
            val value = match.value
            if (value.isNotEmpty() && !value.startsWith('\\') && value.length > 2) {
                result.add(value.substring(1, value.length - 1))
            }
            match = match.next()
        }
        return result
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultLocalizationManager::class.java)
        private val STRING_FOR_LOCALIZATION = Regex("(\\\\)?@\\{(.*?)\\}(\\{(.*?)\\})*")
        private val ARGUMENTS_FOR_STRING_FOR_LOCALIZATION = Regex("(\\\\)?\\{(.*?)\\}")
    }

}