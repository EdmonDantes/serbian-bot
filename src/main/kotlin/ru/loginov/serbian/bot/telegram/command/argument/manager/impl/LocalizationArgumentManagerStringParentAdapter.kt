package ru.loginov.serbian.bot.telegram.command.argument.manager.impl

import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.LocalizationArgumentManager
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext

/**
 * Implementation of [LocalizationArgumentManager] which using [ArgumentManager] which get strings like parent
 */
class LocalizationArgumentManagerStringParentAdapter(
        private val localizationContext: LocalizationContext,
        parent: ArgumentManager<String>,
        shouldAlwaysSendMessage: Boolean = true
) : AbstractLocalizationArgumentManager<String>(parent, shouldAlwaysSendMessage) {
    override fun prepareMessage(message: LocalizationRequest?): String? =
            message?.let { transformMessage(it) }


    override fun transformMessage(message: LocalizationRequest): String =
            localizationContext.localize(message)
}

fun ArgumentManager<String>.withLocalization(localizationContext: LocalizationContext): LocalizationArgumentManager =
        LocalizationArgumentManagerStringParentAdapter(localizationContext, this)

inline fun ArgumentManager<String>.withLocalization(
        localizationContext: LocalizationContext,
        block: LocalizationArgumentManager.() -> Unit
) {
    LocalizationArgumentManagerStringParentAdapter(localizationContext, this).block()
}