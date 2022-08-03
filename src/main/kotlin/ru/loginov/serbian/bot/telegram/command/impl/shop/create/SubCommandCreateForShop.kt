package ru.loginov.serbian.bot.telegram.command.impl.shop.create

import io.github.edmondantes.simple.localization.impl.localizationKey
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.base.ComplexSubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.shop.ShopBotCommand

@Component
@SubCommand([ShopBotCommand::class])
//TODO: Add in next version. After update permission manager. @RequiredPermission("commands.shop.create")
class SubCommandCreateForShop() : ComplexSubCommand() {

    override val commandName: String = "create"
    override fun getActionDescription(context: BotCommandExecuteContext): String? =
            context.localization.localize(localizationKey("bot.command.shop.create._actionDescription"))
}