package ru.loginov.serbian.bot.telegram.command.argument.impl

import ru.loginov.serbian.bot.telegram.command.argument.Argument
import ru.loginov.serbian.bot.telegram.command.argument.ArgumentDefinition
import ru.loginov.serbian.bot.telegram.command.argument.value.ArgumentValue

class DefaultValidateArgument<T>(
        private val parent: Argument<T>,
        private val definition: ArgumentDefinition,
        private val attempts: Int,
        private val validator: (ArgumentValue<T>) -> Boolean
) : Argument<T> {
    override val name: String
        get() = parent.name

    override suspend fun process(): ArgumentValue<T> {
        var value: ArgumentValue<T>
        var currentAttempt = 0
        do {
            value = parent.process()
            currentAttempt++
        } while (!validator(value) && (attempts < 1 || currentAttempt < attempts))

        return value
    }

    override suspend fun get(): T = process().let { if (definition.isOptional) it.getValueOrNull() as T else it.value }

    override fun <R> transformValue(transformer: (ArgumentValue<T>) -> ArgumentValue<R>): Argument<R> =
            DefaultTransformedArgument(this, definition, transformer)

    override fun validateValue(attempts: Int, validator: (ArgumentValue<T>) -> Boolean): Argument<T> =
            DefaultValidateArgument(this, definition, attempts, validator)
}

fun <T> ((T) -> Boolean).forArgumentValue(): (ArgumentValue<T>) -> Boolean = { it.isEmpty || this(it.value) }