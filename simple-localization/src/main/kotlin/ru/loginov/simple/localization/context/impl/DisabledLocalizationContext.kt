package ru.loginov.simple.localization.context.impl

import ru.loginov.simple.localization.LocalizationKey
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext

class DisabledLocalizationContext : LocalizationContext {
    override fun localize(key: LocalizationKey): String = if (key.key.isEmpty()) key.arguments.toString() else key.key
    override fun localize(request: LocalizationRequest): String = request.keys.joinToString { it.key }
    override fun localizeOrNull(key: LocalizationKey): String = localize(key)
    override fun localizeOrDefault(key: LocalizationKey, default: String): String = default
}