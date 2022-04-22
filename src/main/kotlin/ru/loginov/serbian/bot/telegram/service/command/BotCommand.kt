package ru.loginov.serbian.bot.telegram.service.command

import ru.loginov.serbian.bot.telegram.service.command.context.BotCommandExecuteContext
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2

interface BotCommand {

    val commandName: String
    val description: StringBuilderMarkdownV2?
    val usage: StringBuilderMarkdownV2?

    suspend fun execute(context: BotCommandExecuteContext)

}