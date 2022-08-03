package ru.loginov.serbian.bot.telegram.command.argument.manager.impl

import io.github.edmondantes.simple.localization.LocalizationRequest
import io.github.edmondantes.simple.localization.impl.DefaultLocalizationKey
import io.github.edmondantes.simple.localization.impl.DefaultLocalizationRequest
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.LocalizationArgumentManager

open class DefaultLocalizationArgumentManagerWithPrefix(
        prefix: String,
        parent: ArgumentManager<LocalizationRequest>
) : AbstractLocalizationArgumentManager<LocalizationRequest>(parent) {

    private val localizationPrefix = if (prefix.isEmpty()) "" else if (prefix.endsWith('.')) prefix else "$prefix."

    override fun prepareMessage(message: LocalizationRequest?): LocalizationRequest? {
        return message?.let { request ->
            DefaultLocalizationRequest(request.keys.map { key ->
                if (key.key.isNullOrEmpty()) {
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