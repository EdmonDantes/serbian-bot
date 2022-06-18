package ru.loginov.serbian.bot.telegram.command.argument.value.impl

import ru.loginov.serbian.bot.telegram.command.argument.value.ArgumentValue

class DefaultArgumentValue<T> : ArgumentValue<T> {
    override val isNotEmpty: Boolean
    private val _value: T?

    @Suppress("UNCHECKED_CAST")
    override val value: T
        get() = if (isNotEmpty) _value as T else error("Not found value")

    override val isEmpty: Boolean
        get() = !isNotEmpty

    private constructor(hasValue: Boolean, value: T?) {
        this.isNotEmpty = hasValue
        this._value = value
    }

    constructor() : this(false, null)
    constructor(value: T) : this(true, value)

    override fun getValueOrNull(): T? = if (isNotEmpty) value else null
}