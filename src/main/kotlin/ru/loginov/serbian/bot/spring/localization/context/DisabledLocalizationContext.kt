package ru.loginov.serbian.bot.spring.localization.context

class DisabledLocalizationContext : LocalizationContext {
    override fun findLocalizedString(str: String): String? = str
}