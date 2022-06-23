package ru.loginov.serbian.bot.telegram.command.impl.shop.create

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.impl.ComplexSubCommand
import ru.loginov.serbian.bot.telegram.command.impl.shop.ShopBotCommand

@Component
@SubCommand([ShopBotCommand::class])
//TODO: Add in next version. After update permission manager. @RequiredPermission("commands.shop.create")
class SubCommandCreateForShop() : ComplexSubCommand() {

    override val commandName: String = "create"
    override val actionDescription: String = "@{bot.command.shop.create._shopDescription}"
}