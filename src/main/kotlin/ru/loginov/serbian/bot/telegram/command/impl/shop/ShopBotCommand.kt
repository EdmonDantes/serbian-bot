package ru.loginov.serbian.bot.telegram.command.impl.shop

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand

@Component
@RequiredPermission("commands.shop")
class ShopBotCommand : ComplexBotCommand() {
    override val commandName: String = "shop"
    override val shortDescription: String = "@{bot.command.shop._shopDescription}"
}