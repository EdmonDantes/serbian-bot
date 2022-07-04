package ru.loginov.serbian.bot.telegram.command.impl

import ru.loginov.serbian.bot.telegram.command.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext

abstract class LocalizedComplexSubCommand(
        localizedPrefix: String,
        enabledActionDescription: Boolean = true,
        enabledDescription: Boolean = false,
        action: suspend BotCommandExecuteContext.() -> Unit
) : LocalizedComplexBotCommand(localizedPrefix, enabledActionDescription, enabledDescription), SubCommand