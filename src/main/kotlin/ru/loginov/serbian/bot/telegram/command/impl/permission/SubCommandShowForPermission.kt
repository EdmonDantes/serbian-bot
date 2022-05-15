package ru.loginov.serbian.bot.telegram.command.impl.permission

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.impl.ComplexSubCommand

@Component
@SubCommand(parents = [PermissionBotCommand::class])
@RequiredPermission("commands.permission.show")
class SubCommandShowForPermission : ComplexSubCommand() {
    override val commandName: String = "show"
    override val shortDescription: String = "Show groups or permissions"
}