package ru.loginov.serbian.bot.data.manager.localization

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle
import java.util.concurrent.ConcurrentHashMap
import kotlin.streams.toList

//TODO: Will move data to database and add integration with database
@Service
class DefaultLocalizationManager(
        @Value("\${bot.language.support:en;ru}") supportLanguages: String,
        @Value("\${bot.language.default:en}") defaultLanguage: String,
        @Value("\${bot.language.file.base.name:}") private val baseNameForLocalizationFiles: String
) : LocalizationManager {

    override val allSupportLanguages: List<String>
    override val defaultLanguage: String
    private val localeBundle: MutableMap<String, ResourceBundle> = ConcurrentHashMap()

    init {
        allSupportLanguages = supportLanguages.split(';').map { it.lowercase() }
        this.defaultLanguage = defaultLanguage
    }

    override fun languageIsSupport(language: String): Boolean = language.contains(language.lowercase())
    override fun findLocalizedStringByKey(language: String?, key: String): String? =
            try {
                getLocalizationResourceBundle(language ?: defaultLanguage)?.getString(key)
            } catch (e: MissingResourceException) {
                null
            }

    override fun transformStringToLocalized(language: String?, str: String): String =
            str?.replace(STRING_FOR_LOCALIZATION) { match ->
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

    private fun getLocalizationResourceBundle(language: String): ResourceBundle? =
            if (allSupportLanguages.contains(language.lowercase())) {
                localeBundle.compute(language) { _, value ->
                    value ?: loadLocalizationResourceBundle(language)
                }
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