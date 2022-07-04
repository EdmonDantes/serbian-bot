package ru.loginov.simple.localization.context.impl

import ru.loginov.simple.localization.LocalizationKey
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext

abstract class AbstractParentLocalizationContext(protected val parent: LocalizationContext) : LocalizationContext {

    override fun localize(key: LocalizationKey): String =
            parent.localize(prepareKey(key))

    override fun localizeOrNull(key: LocalizationKey): String? =
            parent.localizeOrNull(prepareKey(key))

    override fun localizeOrDefault(key: LocalizationKey, default: String): String =
            parent.localizeOrDefault(prepareKey(key), default)

    override fun localize(request: LocalizationRequest): String =
            parent.localize(prepareRequest(request))

    protected open fun prepareKey(key: LocalizationKey): LocalizationKey = key

    protected open fun prepareRequest(request: LocalizationRequest): LocalizationRequest = request
}