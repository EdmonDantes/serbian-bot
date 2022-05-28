package ru.loginov.serbian.bot.telegram.command.impl.shop

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.dto.shop.ShopDescriptionDto
import ru.loginov.serbian.bot.data.repository.search.SearchRepository
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand

@Component
@SubCommand([ShopBotCommand::class])
class SubCommandFindForShop(
        private val searchRepository: SearchRepository<ShopDescriptionDto>
) : AbstractSubCommand() {
    override val commandName: String = "find"

    override suspend fun execute(context: BotCommandExecuteContext) {

    }
}