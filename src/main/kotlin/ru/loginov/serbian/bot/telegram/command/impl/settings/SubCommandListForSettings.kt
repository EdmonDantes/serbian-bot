package ru.loginov.serbian.bot.telegram.command.impl.settings

import io.github.edmondantes.simple.localization.impl.localizationKey
import ru.loginov.serbian.bot.data.manager.user.UserSettingsManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.base.LocalizedSubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.util.markdown2

//TODO: @Component
@SubCommand([SettingsBotCommand::class])
class SubCommandListForSettings(
        private val userSettingsManager: UserSettingsManager
) : LocalizedSubCommand("bot.command.settings.list") {
    override val commandName: String = "list"

    override suspend fun BotCommandExecuteContext.action() {
        sendMessage {
            markdown2(localization) {
                if (userSettingsManager.possibleSettings.isEmpty()) {
                    append(localizationKey("_.not.found.settings"))
                } else {
                    append(localizationKey("_.all.settings"))
                    append('\n')
                    userSettingsManager.possibleSettings.forEach { setting ->
                        append('\n')
                        append(setting)
                    }
                }
            }
        }
    }
}