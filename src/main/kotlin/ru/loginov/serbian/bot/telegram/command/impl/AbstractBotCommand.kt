package ru.loginov.serbian.bot.telegram.command.impl

import ru.loginov.serbian.bot.telegram.command.BotCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.util.markdown2
import ru.loginov.simple.permissions.annotation.ForcePermissionCheck
import ru.loginov.telegram.api.util.Markdown2StringBuilder

@ForcePermissionCheck
abstract class AbstractBotCommand : BotCommand {
    protected open val actionDescription: String? = null
    protected open val description: Markdown2StringBuilder? = null

    override fun getActionDescription(context: BotCommandExecuteContext): String? =
            actionDescription?.let { context.transformStringToLocalized(it) }

    override fun getDescription(context: BotCommandExecuteContext): Markdown2StringBuilder? =
            description ?: actionDescription?.let { markdown2(context) { append(it) } }
}