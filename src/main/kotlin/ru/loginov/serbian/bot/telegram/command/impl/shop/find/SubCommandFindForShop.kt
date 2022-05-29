package ru.loginov.serbian.bot.telegram.command.impl.shop.find

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.impl.ComplexSubCommand
import ru.loginov.serbian.bot.telegram.command.impl.shop.ShopBotCommand

@Component
@SubCommand([ShopBotCommand::class])
class SubCommandFindForShop : ComplexSubCommand() {
    override val commandName: String = "find"
    override val shortDescription: String = "@{bot.command.shop.find._shopDescription}"
}