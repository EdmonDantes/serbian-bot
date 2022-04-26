package ru.loginov.serbian.bot.telegram.command

import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2

interface BotCommand {

    val commandName: String
    val description: StringBuilderMarkdownV2?
    val usage: StringBuilderMarkdownV2?
    val userAdditionalDataKeys: List<String>

    suspend fun execute(context: BotCommandExecuteContext)

}