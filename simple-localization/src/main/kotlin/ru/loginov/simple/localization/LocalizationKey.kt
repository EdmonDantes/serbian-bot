package ru.loginov.simple.localization

interface LocalizationKey {

    /**
     * Localization key
     */
    val key: String

    /**
     * Argument for localization value.
     */
    val arguments: List<String>

}