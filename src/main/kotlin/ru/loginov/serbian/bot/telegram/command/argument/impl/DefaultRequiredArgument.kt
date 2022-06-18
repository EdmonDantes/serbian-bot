package ru.loginov.serbian.bot.telegram.command.argument.impl

import ru.loginov.serbian.bot.telegram.command.argument.Argument
import ru.loginov.serbian.bot.telegram.command.argument.ArgumentDefinition
import ru.loginov.serbian.bot.telegram.command.argument.value.ArgumentValue

internal class DefaultRequiredArgument<T>(private val parent: ArgumentWithContext<out T?>) : ArgumentWithContext<T> {
    override val name: String
        get() = parent.name

    @Suppress("UNCHECKED_CAST")
    override val function: suspend (ArgumentDefinition) -> ArgumentValue<T> = {
        val value = parent.function(it)
        if (value.isEmpty || value.value == null) {
            error("Argument with name '$name' is required")
        }

        value as ArgumentValue<T>
    }

    override suspend fun process(): ArgumentValue<T> = function(this)
    override suspend fun get(): T = process().value

    override fun <R> transformValue(transformer: (ArgumentValue<T>) -> ArgumentValue<R>): Argument<R> =
            DefaultTransformedArgument(this, this, transformer)

    override fun validateValue(attempts: Int, validator: (ArgumentValue<T>) -> Boolean): Argument<T> =
            DefaultValidateArgument(this, this, attempts, validator)

    override val isOptional: Boolean
        get() = false

    override val isRequired: Boolean
        get() = true

}
