package ru.loginov.simple.localization.manager.impl

class InMemoryLocalizationManager(
        supportLanguages: List<String>,
        private val localizations: Map<String, Map<String, String>>,
        override val defaultLanguage: String
) : AbstractLocalizationManager(supportLanguages) {

    init {
        if (!localizations.keys.containsAll(supportLanguages)) {
            error("Can not initialize InMemoryLocalizationManager, because not all support languages have a localization")
        }
    }

    override fun findByKey(language: String, key: String): String? =
            localizations[language]?.get(key)
}