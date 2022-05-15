package ru.loginov.serbian.bot.spring.localization.context

class DisabledLocalizationContext : LocalizationContext {
    override fun findLocalizedStringByKey(str: String): String? = str
    override fun transformStringToLocalized(str: String): String = str
}