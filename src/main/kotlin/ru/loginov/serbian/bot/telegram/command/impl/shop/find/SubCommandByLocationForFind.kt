package ru.loginov.serbian.bot.telegram.command.impl.shop.find

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.shop.ShopDescriptionManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand

@Component
@SubCommand([SubCommandFindForShop::class])
class SubCommandByLocationForFind(
        private val shopManager: ShopDescriptionManager
) : AbstractSubCommand() {
    override val commandName: String = "byLocation"
    override val shortDescription: String = "@{}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val location = context.getNextLocationArgument("@{}") ?: error("Location can not be null")

        shopManager.findNearest(location.first, location.second)
    }
}