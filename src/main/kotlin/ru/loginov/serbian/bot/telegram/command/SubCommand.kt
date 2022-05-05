package ru.loginov.serbian.bot.telegram.command

interface SubCommand : BotCommand {

    val parentCommandNames: List<String>

}