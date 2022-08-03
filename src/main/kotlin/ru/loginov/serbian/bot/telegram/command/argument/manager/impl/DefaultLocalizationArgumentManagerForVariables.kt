package ru.loginov.serbian.bot.telegram.command.argument.manager.impl

import io.github.edmondantes.simple.localization.LocalizationRequest
import io.github.edmondantes.simple.localization.impl.singleRequest
import ru.loginov.serbian.bot.telegram.command.argument.AnyArgument
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.LocalizationArgumentManager

class DefaultLocalizationArgumentManagerForVariables(
        parent: ArgumentManager<LocalizationRequest>
) : DefaultLocalizationArgumentManagerWithPrefix("_argument", parent) {
    override fun choose(name: String, message: LocalizationRequest?): AnyArgument<Boolean> =
            super.choose(name, message ?: singleRequest(name))

    override fun language(name: String, message: LocalizationRequest?): AnyArgument<String> =
            super.language(name, message ?: singleRequest(name))

    override fun location(name: String, message: LocalizationRequest?): AnyArgument<Pair<Double, Double>> =
            super.location(name, message ?: singleRequest(name))

    override fun argument(name: String, message: LocalizationRequest?): AnyArgument<String> =
            super.argument(name, message ?: singleRequest(name))
}

fun ArgumentManager<LocalizationRequest>.forVariables(): LocalizationArgumentManager =
        DefaultLocalizationArgumentManagerForVariables(this)

fun ArgumentManager<LocalizationRequest>.forVariables(block: LocalizationArgumentManager.() -> Unit) =
        this.forVariables().block()