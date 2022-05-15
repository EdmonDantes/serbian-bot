package ru.loginov.serbian.bot.spring.localization.context

interface LocalizationContext {

    fun findLocalizedString(str: String): String?

}