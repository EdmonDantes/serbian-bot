package ru.loginov.serbian.bot.data.manager.localization

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Locale
import java.util.ResourceBundle
import java.util.concurrent.ConcurrentHashMap

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
    override fun getLocalizationString(language: String?, str: String): String? =
            getLocalizationResourceBundle(language ?: defaultLanguage)?.getString(str)

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


    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultLocalizationManager::class.java)
    }

}