package ru.loginov.serbian.bot.telegram.command.impl.settings

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand

@Component
class SettingsBotCommand : ComplexBotCommand() {
    override val commandName: String = "settings"
    override val shortDescription: String = "@{bot.command.settings._shortDescription}"
}