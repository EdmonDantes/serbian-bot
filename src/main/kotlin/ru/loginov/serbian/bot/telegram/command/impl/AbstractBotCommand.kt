package ru.loginov.serbian.bot.telegram.command.impl

import ru.loginov.serbian.bot.spring.permission.annotation.PermissionCheck
import ru.loginov.serbian.bot.telegram.command.BotCommand
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
import ru.loginov.telegram.api.util.markdown2FromString

@PermissionCheck
abstract class AbstractBotCommand : BotCommand {
    override val shortDescription: String? = null
    override val description: StringBuilderMarkdownV2? = shortDescription?.let { markdown2FromString(it) }
    override val usage: StringBuilderMarkdownV2? = null
}