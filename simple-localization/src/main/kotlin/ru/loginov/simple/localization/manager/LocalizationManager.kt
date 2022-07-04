package ru.loginov.simple.localization.manager

import ru.loginov.simple.localization.LocalizationKey
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext
import ru.loginov.simple.localization.exception.LanguageNotSupportedException

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
     * @param key key for localized string. If [key] have only arguments, method will return concatenation of arguments
     * @return If it found - localized string, else localization key
     * @throws LanguageNotSupportedException if [language] is not support
     */
    @Throws(LanguageNotSupportedException::class)
    fun localize(language: String?, key: LocalizationKey): String

    /**
     * Transform [request] to string with localized parts for [language]
     * @return a string after transformation
     * @throws LanguageNotSupportedException if [language] is not support
     */
    @Throws(LanguageNotSupportedException::class)
    fun localize(language: String?, request: LocalizationRequest): String

    /**
     * Find localized string for [language] by [key]
     * @param language language of localized string (default: 'en' (English))
     * @param key key for localized string. If [key] have only arguments, method will return concatenation of arguments
     * @return If it found - localized string, else null
     * @throws LanguageNotSupportedException if [language] is not support
     */
    @Throws(LanguageNotSupportedException::class)
    fun localizeOrNull(language: String?, key: LocalizationKey): String?

    /**
     * Find localized string for [language] by [key]
     * @param language language of localized string (default: 'en' (English))
     * @param key key for localized string. If [key] have only arguments, method will return concatenation of arguments
     * @param default default value. It will be return if manager can not find localized string for [key]. Can process arguments
     * @return If it found - localized string, else [default]
     * @throws LanguageNotSupportedException if [language] is not support
     */
    @Throws(LanguageNotSupportedException::class)
    fun localizeOrDefault(language: String?, key: LocalizationKey, default: String): String

    /**
     * @return localization context for language
     */
    @Throws(LanguageNotSupportedException::class)
    fun getContext(language: String?): LocalizationContext
}

@Throws(LanguageNotSupportedException::class)
inline fun LocalizationManager.prepareLanguage(language: String?): String {
    if (language != null && !isSupport(language)) {
        throw LanguageNotSupportedException(language)
    }

    return language ?: defaultLanguage
}