package ru.loginov.simple.localization.context

import ru.loginov.simple.localization.LocalizationKey
import ru.loginov.simple.localization.LocalizationRequest

interface LocalizationContext {
    fun localize(key: LocalizationKey): String
    fun localizeOrNull(key: LocalizationKey): String?
    fun localizeOrDefault(key: LocalizationKey, default: String): String
    fun localize(request: LocalizationRequest): String
}