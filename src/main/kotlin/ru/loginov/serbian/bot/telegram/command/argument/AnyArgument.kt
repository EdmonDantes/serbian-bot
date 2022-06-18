package ru.loginov.serbian.bot.telegram.command.argument

interface AnyArgument<T> : Argument<T> {

    fun required(): Argument<T>
    fun optional(): Argument<T>

}

fun <T> AnyArgument<T>.configure(definition: ArgumentDefinition): Argument<T> =
        if (definition.isRequired) {
            required()
        } else {
            optional()
        }

suspend fun <T> AnyArgument<T>.requiredAndGet(): T = required().get()

suspend fun <T> AnyArgument<T>.optionalAndGet(): T? = optional().get()
