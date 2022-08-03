package ru.loginov.serbian.bot.telegram.command.impl.help

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.base.LocalizedComplexBotCommand

@Component
class HelpCommand : LocalizedComplexBotCommand("bot.command.help") {
    override val commandName: String = "help"
    override val canExecuteWithoutSubCommand: Boolean = false
}