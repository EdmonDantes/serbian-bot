package ru.loginov.serbian.bot.telegram.command.impl.help

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2

@Component
class HelpCommand : ComplexBotCommand() {
    override val commandName: String = "help"
    override val shortDescription: String = "Print all bots commands and how to use it"
    override val description: StringBuilderMarkdownV2 =
            StringBuilderMarkdownV2.fromString("Print all bots commands and how to use it")

    override val canExecuteWithoutSubCommand: Boolean = false
}