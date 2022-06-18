package ru.loginov.serbian.bot.telegram.command.argument.impl

import ru.loginov.serbian.bot.telegram.command.argument.Argument
import ru.loginov.serbian.bot.telegram.command.argument.ArgumentDefinition
import ru.loginov.serbian.bot.telegram.command.argument.value.ArgumentValue
import ru.loginov.serbian.bot.telegram.command.argument.value.impl.DefaultArgumentValue

class DefaultTransformedArgument<T, R>(
        private val parent: Argument<T>,
        private val definition: ArgumentDefinition,
        private val transformer: (ArgumentValue<T>) -> ArgumentValue<R>
) : Argument<R> {

    override val name: String
        get() = parent.name

    override suspend fun process(): ArgumentValue<R> = transformer(parent.process())

    override suspend fun get(): R = process().let { if (definition.isOptional) it.getValueOrNull() as R else it.value }

    override fun <R1> transformValue(transformer: (ArgumentValue<R>) -> ArgumentValue<R1>): Argument<R1> =
            DefaultTransformedArgument(this, definition, transformer)

    override fun validateValue(attempts: Int, validator: (ArgumentValue<R>) -> Boolean): Argument<R> =
            DefaultValidateArgument(this, definition, attempts, validator)

}

@Suppress("UNCHECKED_CAST")
fun <T, R> ((T) -> R).forArgumentValue(): (ArgumentValue<T>) -> ArgumentValue<R> = {
    if (it.isEmpty) {
        it as ArgumentValue<R>
    } else {
        DefaultArgumentValue(this(it.value))
    }
}