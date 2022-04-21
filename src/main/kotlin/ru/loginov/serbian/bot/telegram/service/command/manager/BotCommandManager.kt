package ru.loginov.serbian.bot.telegram.service.command.manager

import ru.loginov.serbian.bot.telegram.service.command.BotCommand

interface BotCommandManager {

    fun getAllCommands() : List<BotCommand>
    fun getCommandByName(name: String) : BotCommand?

}