package ru.loginov.simple.localization.manager.impl

import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle

class ResourceBundleLocalizationManager(
        supportLanguages: List<String>,
        override val defaultLanguage: String,
        private val resourceBundlePrefix: String = "localization"
) : AbstractLocalizationManager(supportLanguages) {

    private val bundles: Map<String, ResourceBundle>

    init {
        bundles = this.supportLanguages.associateWith {
            loadLocalizationResourceBundle(it)
        }
    }

    override fun findByKey(language: String, key: String): String? =
            try {
                bundles[language]?.getString(key)
            } catch (e: MissingResourceException) {
                null
            }


    private fun loadLocalizationResourceBundle(language: String): ResourceBundle =
            try {
                ResourceBundle.getBundle(resourceBundlePrefix, Locale.forLanguageTag(language))
            } catch (e: Exception) {
                error("Can not load resource bundle with base name '$resourceBundlePrefix' and language '$language'")
            }
}