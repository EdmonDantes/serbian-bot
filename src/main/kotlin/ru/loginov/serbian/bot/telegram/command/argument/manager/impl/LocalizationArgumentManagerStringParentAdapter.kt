package ru.loginov.serbian.bot.telegram.command.argument.manager.impl

import io.github.edmondantes.simple.localization.LocalizationRequest
import io.github.edmondantes.simple.localization.context.LocalizationContext
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.LocalizationArgumentManager

/**
 * Implementation of [LocalizationArgumentManager] which using [ArgumentManager] which get strings like parent
 */
class LocalizationArgumentManagerStringParentAdapter(
        private val localizationContext: LocalizationContext,
        parent: ArgumentManager<String>
) : AbstractLocalizationArgumentManager<String>(parent) {
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