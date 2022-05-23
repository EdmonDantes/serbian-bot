package ru.loginov.serbian.bot.data.manager.localization.exception

class LanguageNotSupportedException(val language: String) : RuntimeException("Language '$language' is not supported") {
}