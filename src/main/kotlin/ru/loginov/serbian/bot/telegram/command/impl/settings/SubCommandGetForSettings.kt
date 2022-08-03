package ru.loginov.serbian.bot.telegram.command.impl.settings

import ru.loginov.serbian.bot.data.manager.user.UserSettingsManager
import ru.loginov.serbian.bot.telegram.command.base.LocalizedSubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext

//TODO:
class SubCommandGetForSettings(
        private val userSettingsManager: UserSettingsManager
) : LocalizedSubCommand("bot.command.settings.get") {
    override val commandName: String = "get"

    override suspend fun BotCommandExecuteContext.action() {
        if (userSettingsManager.possibleSettings.isEmpty()) {
            val settingField = arguments.argument("settingField")
        }
    }
}