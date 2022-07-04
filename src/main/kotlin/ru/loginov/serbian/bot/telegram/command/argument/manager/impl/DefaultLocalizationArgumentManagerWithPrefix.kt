package ru.loginov.serbian.bot.telegram.command.argument.manager.impl

import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.LocalizationArgumentManager
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.impl.DefaultLocalizationKey
import ru.loginov.simple.localization.impl.DefaultLocalizationRequest

class DefaultLocalizationArgumentManagerWithPrefix(
        prefix: String,
        parent: ArgumentManager<LocalizationRequest>,
        shouldAlwaysSendMessage: Boolean = true
) : AbstractLocalizationArgumentManager<LocalizationRequest>(parent, shouldAlwaysSendMessage) {

    private val localizationPrefix = if (prefix.isEmpty()) "" else if (prefix.endsWith('.')) prefix else "$prefix."

    override fun prepareMessage(message: LocalizationRequest?): LocalizationRequest? {
        return message?.let { request ->
            DefaultLocalizationRequest(request.keys.map { key ->
                if (key.key.isEmpty()) {
                    key
                } else {
                    DefaultLocalizationKey("$localizationPrefix${key.key}", key.arguments)
                }
            })
        }
    }

    override fun transformMessage(message: LocalizationRequest): LocalizationRequest? = message
}

fun ArgumentManager<LocalizationRequest>.withPrefix(prefix: String): LocalizationArgumentManager =
        DefaultLocalizationArgumentManagerWithPrefix(prefix, this)

fun ArgumentManager<LocalizationRequest>.withPrefix(prefix: String, block: LocalizationArgumentManager.() -> Unit) =
        this.withPrefix(prefix).block()

fun ArgumentManager<LocalizationRequest>.forVariables(): LocalizationArgumentManager =
        DefaultLocalizationArgumentManagerWithPrefix("_argument", this)

fun ArgumentManager<LocalizationRequest>.forVariables(block: LocalizationArgumentManager.() -> Unit) =
        this.forVariables().block()