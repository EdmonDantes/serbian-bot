package ru.loginov.serbian.bot.telegram.command.argument

interface ArgumentDefinition {

    val isOptional: Boolean
    val isRequired: Boolean

    val additionalProperties: Map<String, String>
        get() = emptyMap()

}