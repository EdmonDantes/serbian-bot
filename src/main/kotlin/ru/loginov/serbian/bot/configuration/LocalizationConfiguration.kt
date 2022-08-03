package ru.loginov.serbian.bot.configuration

import io.github.edmondantes.simple.localization.Localizer
import io.github.edmondantes.simple.localization.impl.ResourceBundleLocalizer
import io.github.edmondantes.simple.localization.parser.LocalizationRequestParser
import io.github.edmondantes.simple.localization.parser.impl.DefaultLocalizationRequestParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
//TODO: Will move data to database and add integration with database
class LocalizationConfiguration {

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
    @Value("\${bot.language.file.base.name:lang/localization}")
    private var baseNameForLocalizationFiles: String = "lang/localization"

    @Value("\${bot.language.support:en;ru}")
    fun setSupportLanguages(supportLanguages: String) {
        this.supportLanguages = supportLanguages.split(';').filter { it.length > 1 }.map { it.lowercase() }
    }

    @Bean
    fun localizationManager(): Localizer =
            ResourceBundleLocalizer(supportLanguages, defaultLanguage, baseNameForLocalizationFiles)

    @Bean
    fun localizationRequestParser(): LocalizationRequestParser =
            DefaultLocalizationRequestParser()
}