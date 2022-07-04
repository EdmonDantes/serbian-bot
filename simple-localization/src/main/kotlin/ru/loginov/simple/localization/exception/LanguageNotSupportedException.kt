package ru.loginov.simple.localization.exception

class LanguageNotSupportedException(val language: String) : RuntimeException("Language '$language' is not supported")