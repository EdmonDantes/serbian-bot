package ru.loginov.serbian.bot.data.manager.localization

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DefaultLocalizationManager(
        @Value("\${bot.language.support:en;ru}") supportLanguages: String,
        @Value("\${bot.language.default:en}") defaultLanguage: String
) : LocalizationManager {

    override val allSupportLanguages: List<String>
    override val defaultLanguage: String

    init {
        allSupportLanguages = supportLanguages.split(';').map { it.lowercase() }
        this.defaultLanguage = defaultLanguage
    }

    override fun languageIsSupport(language: String): Boolean = language.contains(language.lowercase())

}