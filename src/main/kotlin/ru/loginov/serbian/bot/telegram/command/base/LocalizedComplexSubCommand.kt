package ru.loginov.serbian.bot.telegram.command.base

import ru.loginov.serbian.bot.telegram.command.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.simple.permissions.annotation.ForcePermissionCheck

@ForcePermissionCheck
abstract class LocalizedComplexSubCommand(
        localizedPrefix: String,
        enabledActionDescription: Boolean = true,
        enabledDescription: Boolean = false,
        action: suspend BotCommandExecuteContext.() -> Unit
) : LocalizedComplexBotCommand(localizedPrefix, enabledActionDescription, enabledDescription), SubCommand