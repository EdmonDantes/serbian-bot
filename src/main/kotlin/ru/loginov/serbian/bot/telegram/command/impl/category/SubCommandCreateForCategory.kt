package ru.loginov.serbian.bot.telegram.command.impl.category

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand

@Component
@SubCommand(parent = CategoryBotCommand::class, subCommandName = "create")
@RequiredPermission("commands.category.create")
class SubCommandCreateForCategory : AbstractSubCommand() {
    override val commandName: String = "create"


    override suspend fun execute(context: BotCommandExecuteContext) {

    }
}