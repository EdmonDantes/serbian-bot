package ru.loginov.serbian.bot.telegram.command.impl

import ru.loginov.serbian.bot.telegram.command.BotCommand
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2

abstract class AbstractBotCommand : BotCommand {
    override val description: StringBuilderMarkdownV2? = null
    override val usage: StringBuilderMarkdownV2? = null
    override val userAdditionalDataKeys: List<String> = emptyList()
}