package ru.loginov.simple.localization.manager.impl

import ru.loginov.simple.localization.LocalizationKey
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext
import ru.loginov.simple.localization.manager.LocalizationManager

abstract class AbstractParentLocalizationManager(
        protected val parent: LocalizationManager
) : LocalizationManager {

    override val supportLanguages: List<String>
        get() = parent.supportLanguages
    override val defaultLanguage: String
        get() = parent.defaultLanguage

    override fun isSupport(language: String): Boolean =
            parent.isSupport(language)

    override fun localize(language: String?, key: LocalizationKey): String =
            parent.localize(language, prepareKey(key))

    override fun localize(language: String?, request: LocalizationRequest): String =
            parent.localize(language, prepareRequest(request))

    override fun localizeOrNull(language: String?, key: LocalizationKey): String? =
            parent.localizeOrNull(language, prepareKey(key))

    override fun localizeOrDefault(language: String?, key: LocalizationKey, default: String): String =
            parent.localizeOrDefault(language, prepareKey(key), default)

    override fun getContext(language: String?): LocalizationContext =
            parent.getContext(language)

    protected open fun prepareKey(key: LocalizationKey): LocalizationKey = key
    protected open fun prepareRequest(request: LocalizationRequest) = request
}