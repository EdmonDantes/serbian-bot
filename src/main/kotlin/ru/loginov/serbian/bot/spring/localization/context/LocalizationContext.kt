package ru.loginov.serbian.bot.spring.localization.context

interface LocalizationContext {

    fun findLocalizedStringByKey(str: String): String?
    fun transformStringToLocalized(str: String): String

}