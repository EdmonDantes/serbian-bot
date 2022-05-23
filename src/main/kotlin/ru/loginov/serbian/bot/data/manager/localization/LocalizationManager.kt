package ru.loginov.serbian.bot.data.manager.localization

import ru.loginov.serbian.bot.data.manager.localization.exception.LanguageNotSupportedException

/**
 * Class for manage localization
 */
interface LocalizationManager {

    /**
     * All support languages
     */
    val supportLanguages: List<String>

    /**
     * Default language
     */
    val defaultLanguage: String

    /**
     * Check application supports [language]
     * @return True if application supports [language], else false
     */
    fun isSupport(language: String): Boolean

    /**
     * Find localized string for [language] by [key]
     * @param language language of localized string (default: 'en' (English))
     * @param key key for localized string
     * @return If it found - localized string, else null
     * @throws LanguageNotSupportedException [language] is not support
     */
    @Throws(LanguageNotSupportedException::class)
    fun findLocalizedStringByKey(language: String?, key: String): String?

    /**
     * Transform [str] to string with localized parts for [language]
     *
     * Format:
     * `@{key.for.localization}{arg1}{arg2}`
     *
     * Please use \ for ignore transformation
     *
     * @return a string after transformation
     * @throws LanguageNotSupportedException [language] is not support
     */
    @Throws(LanguageNotSupportedException::class)
    fun transformStringToLocalized(language: String?, str: String): String

}