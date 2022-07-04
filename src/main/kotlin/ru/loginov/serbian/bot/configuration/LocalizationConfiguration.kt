package ru.loginov.serbian.bot.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.loginov.simple.localization.manager.LocalizationManager
import ru.loginov.simple.localization.manager.impl.ResourceBundleLocalizationManager
import ru.loginov.simple.localization.parser.LocalizationRequestParser
import ru.loginov.simple.localization.parser.impl.DefaultLocalizationRequestParser

@Configuration
//TODO: Will move data to database and add integration with database
open class LocalizationConfiguration {

    /**
     * Which languages is supported by application
     */
    private var supportLanguages: List<String> = emptyList()

    /**
     * Default language
     */
    @Value("\${bot.language.default:en}")
    private var defaultLanguage: String = "en"

    /**
     * Base name for resources bundle for localization
     */
    @Value("\${bot.language.file.base.name:}")
    private var baseNameForLocalizationFiles: String = "lang/localization"

    @Value("\${bot.language.support:en;ru}")
    fun setSupportLanguages(supportLanguages: String) {
        this.supportLanguages = supportLanguages.split(';').filter { it.length > 1 }.map { it.lowercase() }
    }

    @Bean
    fun localizationManager(): LocalizationManager =
            ResourceBundleLocalizationManager(supportLanguages, defaultLanguage, baseNameForLocalizationFiles)

    @Bean
    fun localizationRequestParser(): LocalizationRequestParser =
            DefaultLocalizationRequestParser()
}