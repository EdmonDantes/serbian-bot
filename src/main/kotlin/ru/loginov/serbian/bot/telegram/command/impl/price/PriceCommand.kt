package ru.loginov.serbian.bot.telegram.command.impl.price

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand
import ru.loginov.simple.localization.impl.localizationKey

@Component
class PriceCommand : ComplexBotCommand() {
    override val commandName: String = "price"
    override fun getActionDescription(context: BotCommandExecuteContext): String? =
            context.localization.localize(localizationKey("bot.command.price._actionDescription"))
}