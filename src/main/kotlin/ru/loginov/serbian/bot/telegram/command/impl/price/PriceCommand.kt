package ru.loginov.serbian.bot.telegram.command.impl.price

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand

@Component
class PriceCommand : ComplexBotCommand() {
    override val commandName: String = "price"
    override val actionDescription: String? = "@{bot.command.price._shopDescription}"
}