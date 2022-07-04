package ru.loginov.simple.localization.manager.impl

import ru.loginov.simple.localization.LocalizationKey
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.impl.DefaultLocalizationKey
import ru.loginov.simple.localization.impl.DefaultLocalizationRequest
import ru.loginov.simple.localization.manager.LocalizationManager

open class DefaultParentLocalizationManagerWithPrefix(
        parent: LocalizationManager,
        prefix: String?
) : AbstractParentLocalizationManager(parent) {

    private val prefix: String = preparePrefix(prefix)

    override fun prepareKey(key: LocalizationKey): LocalizationKey {
        if (key.key.isBlank()) {
            return key
        }

        return DefaultLocalizationKey(prefix + key.key, key.arguments)
    }

    override fun prepareRequest(request: LocalizationRequest): LocalizationRequest {
        if (request.keys.isEmpty()) {
            return request
        }

        return DefaultLocalizationRequest(request.keys.map { prepareKey(it) })
    }

    protected open fun preparePrefix(prefix: String?): String =
            if (prefix.isNullOrBlank()) "" else if (prefix.endsWith('.')) prefix else "$prefix."
}


fun LocalizationManager.withPrefix(prefix: String): LocalizationManager =
        DefaultParentLocalizationManagerWithPrefix(this, prefix)

fun LocalizationManager.withPrefix(prefix: String, block: LocalizationManager.() -> Unit): Unit =
        withPrefix(prefix).block()