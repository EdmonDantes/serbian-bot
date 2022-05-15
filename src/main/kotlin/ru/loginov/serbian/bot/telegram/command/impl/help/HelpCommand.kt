package ru.loginov.serbian.bot.telegram.command.impl.help

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand

@Component
class HelpCommand : ComplexBotCommand() {
    override val commandName: String = "help"
    override val shortDescription: String = "@{bot.command.help._shortDescription}"
    override val canExecuteWithoutSubCommand: Boolean = false
}