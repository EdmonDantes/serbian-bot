package ru.loginov.serbian.bot.telegram.command.argument.manager.impl

import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.LocalizationArgumentManager
import ru.loginov.simple.localization.LocalizationRequest

/**
 * Implementation of [LocalizationArgumentManager] which using [ArgumentManager] which get strings like parent
 */
class LocalizationArgumentManagerLocalizationParentAdapter(
        parent: ArgumentManager<LocalizationRequest>,
        shouldAlwaysSendMessage: Boolean = true
) : AbstractLocalizationArgumentManager<LocalizationRequest>(parent, shouldAlwaysSendMessage) {
    override fun prepareMessage(message: LocalizationRequest?): LocalizationRequest? = message
    override fun transformMessage(message: LocalizationRequest): LocalizationRequest? = message
}

fun ArgumentManager<LocalizationRequest>.withLocalization(): LocalizationArgumentManager =
        if (this is LocalizationArgumentManager)
            this
        else
            LocalizationArgumentManagerLocalizationParentAdapter(this)

inline fun ArgumentManager<LocalizationRequest>.withLocalization(block: LocalizationArgumentManager.() -> Unit) {
    val manager =
            if (this is LocalizationArgumentManager)
                this
            else
                LocalizationArgumentManagerLocalizationParentAdapter(this)

    manager.block()
}