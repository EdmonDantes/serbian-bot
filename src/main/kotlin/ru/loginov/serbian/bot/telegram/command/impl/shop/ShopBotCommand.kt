package ru.loginov.serbian.bot.telegram.command.impl.shop

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand
import ru.loginov.simple.localization.impl.localizationKey

@Component
//TODO: Add in next version. After update permission manager. @RequiredPermission("commands.shop")
class ShopBotCommand : ComplexBotCommand() {
    override val commandName: String = "shop"
    override fun getActionDescription(context: BotCommandExecuteContext): String? =
            context.localization.localize(localizationKey("bot.command.shop._actionDescription"))
}