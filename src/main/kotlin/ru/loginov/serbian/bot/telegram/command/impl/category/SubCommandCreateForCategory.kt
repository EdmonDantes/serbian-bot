package ru.loginov.serbian.bot.telegram.command.impl.category

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand

@Component
class SubCommandCreateForCategory : AbstractSubCommand() {
    override val commandName: String = "create"

    override suspend fun execute(context: BotCommandExecuteContext) {
        TODO("Not yet implemented")
    }
}