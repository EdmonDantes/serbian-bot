package ru.loginov.simple.localization.context.impl

import ru.loginov.simple.localization.LocalizationKey
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext
import ru.loginov.simple.localization.impl.DefaultLocalizationKey
import ru.loginov.simple.localization.impl.DefaultLocalizationRequest

open class DefaultParentLocalizationContextWithPrefix(
        parent: LocalizationContext,
        prefix: String?
) : AbstractParentLocalizationContext(parent) {

    private val prefix = preparePrefix(prefix)

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

fun LocalizationContext.withPrefix(prefix: String): LocalizationContext =
        DefaultParentLocalizationContextWithPrefix(this, prefix)

fun LocalizationContext.withPrefix(prefix: String, block: LocalizationContext.() -> Unit): Unit =
        withPrefix(prefix).block()