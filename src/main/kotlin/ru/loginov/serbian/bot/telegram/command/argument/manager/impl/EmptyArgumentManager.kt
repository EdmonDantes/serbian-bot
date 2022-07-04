package ru.loginov.serbian.bot.telegram.command.argument.manager.impl

import ru.loginov.serbian.bot.telegram.command.argument.AnyArgument
import ru.loginov.serbian.bot.telegram.command.argument.impl.EmptyArgument
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager

class EmptyArgumentManager : ArgumentManager<Any?> {
    override fun choose(name: String, message: Any?): AnyArgument<Boolean> =
            EmptyArgument(name)

    override fun language(name: String, message: Any?): AnyArgument<String> =
            EmptyArgument(name)

    override fun location(name: String, message: Any?): AnyArgument<Pair<Double, Double>> =
            EmptyArgument(name)

    override fun argument(name: String, message: Any?): AnyArgument<String> =
            EmptyArgument(name)

    override fun argument(name: String, variants: List<Any?>, message: Any?): AnyArgument<String> =
            EmptyArgument(name)

    override fun argument(name: String, variants: Map<Any?, String>, message: Any?): AnyArgument<String> =
            EmptyArgument(name)
}