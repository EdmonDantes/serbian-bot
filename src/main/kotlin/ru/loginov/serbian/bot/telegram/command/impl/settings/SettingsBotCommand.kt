package ru.loginov.serbian.bot.telegram.command.impl.settings

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand

@Component
class SettingsBotCommand : ComplexBotCommand() {
    override val commandName: String = "settings"
    override val actionDescription: String = "@{bot.command.settings._shortDescription}"
}