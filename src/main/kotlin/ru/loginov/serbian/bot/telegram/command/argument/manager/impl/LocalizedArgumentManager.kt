package ru.loginov.serbian.bot.telegram.command.argument.manager.impl

import ru.loginov.serbian.bot.telegram.command.argument.AnyArgument
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager

class LocalizedArgumentManager(
        private val localizationPrefix: String,
        private val parent: ArgumentManager
) : ArgumentManager {
    override fun choose(name: String, message: String?): AnyArgument<Boolean> =
            parent.choose(name, message?.let { "@{$localizationPrefix.$it}" } ?: "@{$localizationPrefix.$name}")

    override fun language(name: String, message: String?): AnyArgument<String> =
            parent.language(name, message?.let { "@{$localizationPrefix.$it}" })

    override fun location(name: String, message: String?): AnyArgument<Pair<Double, Double>> =
            parent.location(name, message?.let { "@{$localizationPrefix.$it}" })

    override fun argument(name: String, message: String?): AnyArgument<String> =
            parent.argument(name, message?.let { "@{$localizationPrefix.$it}" })

    override fun argument(name: String, variants: List<String>, message: String?): AnyArgument<String> =
            parent.argument(name, variants, message?.let { "@{$localizationPrefix.$it}" })

    override fun argument(name: String, variants: Map<String, String>, message: String?): AnyArgument<String> =
            parent.argument(name, variants, message?.let { "@{$localizationPrefix.$it}" })
}


inline fun ArgumentManager.withLocalization(localizationPrefix: String, func: LocalizedArgumentManager.() -> Unit) {
    LocalizedArgumentManager(localizationPrefix, this).apply(func)
}