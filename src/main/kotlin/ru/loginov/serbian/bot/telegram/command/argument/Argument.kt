package ru.loginov.serbian.bot.telegram.command.argument

import ru.loginov.serbian.bot.telegram.command.argument.impl.forArgumentValue
import ru.loginov.serbian.bot.telegram.command.argument.value.ArgumentValue

interface Argument<T> {
    val name: String

    suspend fun process(): ArgumentValue<T>
    suspend fun get(): T
    suspend fun getOrNull(): T? = process().getValueOrNull()

    fun <R> transformValue(transformer: (ArgumentValue<T>) -> ArgumentValue<R>): Argument<R>

    // Ignored, if value is empty
    fun <R> transform(transformer: (T) -> R): Argument<R> = transformValue(transformer.forArgumentValue())

    fun validateValue(attempts: Int = 3, validator: (ArgumentValue<T>) -> Boolean): Argument<T>

    // Ignored, if value is empty
    fun validate(attempts: Int = 3, validator: (T) -> Boolean): Argument<T> = validateValue(
            attempts,
            validator.forArgumentValue()
    )
}