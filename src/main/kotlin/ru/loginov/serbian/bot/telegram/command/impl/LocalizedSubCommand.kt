package ru.loginov.serbian.bot.telegram.command.impl

import ru.loginov.serbian.bot.telegram.command.SubCommand

abstract class LocalizedSubCommand(
        localizedPrefix: String,
        enabledActionDescription: Boolean = true,
        enabledDescription: Boolean = false,
) : LocalizedBotCommand(localizedPrefix, enabledActionDescription, enabledDescription), SubCommand {}