package ru.loginov.simple.localization.context.impl

import ru.loginov.simple.localization.LocalizationKey
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext
import ru.loginov.simple.localization.manager.LocalizationManager

class DefaultLocalizationContext(
        private val language: String,
        private val localizationManager: LocalizationManager
) : LocalizationContext {
    override fun localize(key: LocalizationKey): String =
            localizationManager.localize(language, key)

    override fun localizeOrNull(key: LocalizationKey): String? =
            localizationManager.localizeOrNull(language, key)

    override fun localizeOrDefault(key: LocalizationKey, default: String): String =
            localizationManager.localizeOrDefault(language, key, default)

    override fun localize(request: LocalizationRequest): String =
            localizationManager.localize(language, request)
}