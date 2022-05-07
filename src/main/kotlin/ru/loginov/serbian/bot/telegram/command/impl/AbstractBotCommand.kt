package ru.loginov.serbian.bot.telegram.command.impl

import ru.loginov.serbian.bot.spring.permission.annotation.PermissionCheck
import ru.loginov.serbian.bot.telegram.command.BotCommand
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2

@PermissionCheck
abstract class AbstractBotCommand : BotCommand {
    override val shortDescription: String? = null
    override val description: StringBuilderMarkdownV2? = null
    override val usage: StringBuilderMarkdownV2? = null
}