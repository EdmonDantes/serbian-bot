package ru.loginov.serbian.bot.telegram.command.impl

abstract class AbstractSubCommand : AbstractBotCommand() {
    final override val isSubCommand: Boolean = true
}