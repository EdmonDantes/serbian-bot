package ru.loginov.serbian.bot.telegram.command.impl.price

import io.github.edmondantes.simple.localization.impl.localizationKey
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.base.ComplexBotCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext

@Component
class PriceCommand : ComplexBotCommand() {
    override val commandName: String = "price"
    override fun getActionDescription(context: BotCommandExecuteContext): String? =
            context.localization.localize(localizationKey("bot.command.price._actionDescription"))
}