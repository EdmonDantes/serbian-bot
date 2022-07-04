package ru.loginov.serbian.bot.telegram.command.argument.manager.impl

import ru.loginov.serbian.bot.telegram.command.argument.AnyArgument
import ru.loginov.serbian.bot.telegram.command.argument.ArgumentDefinition
import ru.loginov.serbian.bot.telegram.command.argument.configure
import ru.loginov.serbian.bot.telegram.command.argument.impl.DefaultArgument
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.value.ArgumentValue
import ru.loginov.serbian.bot.telegram.command.argument.value.impl.DefaultArgumentValue

/**
 * Convert text message to arguments
 *
 * Not thread safe
 */
class StringArgumentManager(private val parent: ArgumentManager<String>?, args: String) : ArgumentManager<String> {

    private val arguments: List<String>
    private var index: Int = 0

    init {
        val arguments = ArrayList<String>()
        var builder = StringBuilder()
        var ignoreSpaces = false
        args.forEachIndexed { index, ch ->
            if (ch == '\'' && (index == 0 || args[index - 1] != '\\')) {
                if (ignoreSpaces) {
                    arguments.add(builder.toString())
                    builder = StringBuilder()
                } else {
                    ignoreSpaces = true
                }
            } else if (ch == ' ') {
                if (ignoreSpaces) {
                    builder.append(ch)
                } else if (builder.isNotEmpty()) {
                    arguments.add(builder.toString())
                    builder = StringBuilder()
                }
            } else {
                builder.append(ch)
            }
        }

        if (builder.isNotEmpty()) {
            arguments.add(builder.toString())
        }

        this.arguments = arguments
    }

    override fun choose(name: String, message: String?): AnyArgument<Boolean> =
            getArgument(name, { choose(name, message) }) { it.toBoolean() }

    override fun language(name: String, message: String?): AnyArgument<String> =
            getArgument(name, { language(name, message) }) { it }

    override fun location(name: String, message: String?): AnyArgument<Pair<Double, Double>> =
            getArgument(name, { location(name, message) }) {
                it.split(';').let { it[0].toDouble() to it[1].toDouble() }
            }

    override fun argument(name: String, message: String?): AnyArgument<String> =
            getArgument(name, { argument(name, message) }) { it }

    override fun argument(name: String, variants: List<String>, message: String?): AnyArgument<String> =
            getArgument(name, { argument(name, variants, message) }) { it }

    override fun argument(name: String, variants: Map<String, String>, message: String?): AnyArgument<String> =
            getArgument(name, { argument(name, variants, message) }) { it }

    private fun <T> getArgument(
            name: String,
            parentFunction: ArgumentManager<String>.() -> AnyArgument<T>,
            transformer: (String) -> T
    ): AnyArgument<T> = (DefaultArgument(name) { getArgumentValue(it, parentFunction, transformer) })


    private suspend fun <T> getArgumentValue(
            definition: ArgumentDefinition,
            parentFunction: ArgumentManager<String>.() -> AnyArgument<T>,
            transformer: (String) -> T?
    ): ArgumentValue<T> =
            if (index >= arguments.size) {
                if (parent != null) {
                    parentFunction(parent).configure(definition).process()
                } else {
                    throw IndexOutOfBoundsException("Can not get next argument. The last argument already was returned")
                }
            } else {
                transformer(arguments[index++])?.let { DefaultArgumentValue(it) } ?: ArgumentValue.empty()
            }
}