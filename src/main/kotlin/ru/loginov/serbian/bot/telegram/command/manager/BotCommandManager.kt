package ru.loginov.serbian.bot.telegram.command.manager

import ru.loginov.serbian.bot.telegram.command.BotCommand

interface BotCommandManager {

    fun getAllCommands() : List<BotCommand>
    fun getCommandByName(name: String) : BotCommand?

}