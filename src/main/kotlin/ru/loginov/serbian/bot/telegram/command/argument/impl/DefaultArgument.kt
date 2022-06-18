package ru.loginov.serbian.bot.telegram.command.argument.impl

import ru.loginov.serbian.bot.telegram.command.argument.AnyArgument
import ru.loginov.serbian.bot.telegram.command.argument.Argument
import ru.loginov.serbian.bot.telegram.command.argument.ArgumentDefinition
import ru.loginov.serbian.bot.telegram.command.argument.value.ArgumentValue

class DefaultArgument<T>(
        override val name: String,
        override val function: suspend (ArgumentDefinition) -> ArgumentValue<T>
) : AnyArgument<T>, ArgumentWithContext<T> {

    override val isOptional: Boolean
        get() = true
    override val isRequired: Boolean
        get() = false

    override suspend fun process(): ArgumentValue<T> = function(this)
    override suspend fun get(): T = process().value

    override fun required(): Argument<T> = DefaultRequiredArgument(this)
    override fun optional(): Argument<T> = this

    override fun <R> transformValue(transformer: (ArgumentValue<T>) -> ArgumentValue<R>): Argument<R> =
            DefaultTransformedArgument(this, this, transformer)

    override fun validateValue(attempts: Int, validator: (ArgumentValue<T>) -> Boolean): Argument<T> =
            DefaultValidateArgument(this, this, attempts, validator)
}