package ru.loginov.serbian.bot.telegram.command.impl.settings

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.base.LocalizedComplexBotCommand

@Component
class SettingsBotCommand : LocalizedComplexBotCommand("bot.command.settings") {
    override val commandName: String = "settings"
}