package ru.loginov.serbian.bot.telegram.command.argument.impl

import ru.loginov.serbian.bot.telegram.command.argument.Argument
import ru.loginov.serbian.bot.telegram.command.argument.ArgumentDefinition
import ru.loginov.serbian.bot.telegram.command.argument.value.ArgumentValue

internal interface ArgumentWithContext<T> : ArgumentDefinition, Argument<T> {
    val function: suspend (ArgumentDefinition) -> ArgumentValue<T>
}