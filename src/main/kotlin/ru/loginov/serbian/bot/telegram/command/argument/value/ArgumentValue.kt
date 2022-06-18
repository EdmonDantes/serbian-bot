package ru.loginov.serbian.bot.telegram.command.argument.value

import ru.loginov.serbian.bot.telegram.command.argument.value.impl.DefaultArgumentValue

interface ArgumentValue<T> {

    val isNotEmpty: Boolean
    val isEmpty: Boolean
    val value: T

    fun getValueOrNull(): T?
    fun getValueOr(supplier: () -> T): T = getValueOrNull() ?: supplier()

    companion object {
        val EMPTY = object : ArgumentValue<Any?> {
            override val isNotEmpty: Boolean = false
            override val isEmpty: Boolean = true
            override val value: Any? = null

            override fun getValueOrNull(): Any? = null
        }

        @Suppress("UNCHECKED_CAST")
        inline fun <T> empty(): ArgumentValue<T> = EMPTY as ArgumentValue<T>
    }
}

inline fun <T, R> ArgumentValue<T>.transform(transformer: (T) -> R): ArgumentValue<R> =
        if (isEmpty) this as ArgumentValue<R> else DefaultArgumentValue(transformer(value))

inline fun <T, R> ArgumentValue<T>.transformOrEmpty(transformer: (T) -> R?): ArgumentValue<R> =
        if (isEmpty) this as ArgumentValue<R> else transformer(value)?.let { DefaultArgumentValue(it) }
                ?: ArgumentValue.empty()