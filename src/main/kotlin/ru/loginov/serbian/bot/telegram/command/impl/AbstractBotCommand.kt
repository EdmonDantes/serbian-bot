package ru.loginov.serbian.bot.telegram.command.impl

import ru.loginov.serbian.bot.telegram.command.BotCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.simple.permissions.annotation.ForcePermissionCheck
import ru.loginov.telegram.api.util.Markdown2StringBuilder

@ForcePermissionCheck
abstract class AbstractBotCommand : BotCommand {

    override fun getActionDescription(context: BotCommandExecuteContext): String? = null
    override fun getDescription(context: BotCommandExecuteContext): Markdown2StringBuilder? =
            getActionDescription(context)?.let {
                ru.loginov.telegram.api.util.impl.markdown2 {
                    append(it)
                }
            }
}