package ru.loginov.serbian.bot.data.manager.localization

interface LocalizationManager {

    val allSupportLanguages: List<String>
    val defaultLanguage: String

    fun languageIsSupport(language: String): Boolean
    fun findLocalizedStringByKey(language: String?, key: String): String?
    fun transformStringToLocalized(language: String?, str: String): String

}