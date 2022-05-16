package ru.loginov.serbian.bot.telegram.command.impl.permission.users

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.impl.ComplexSubCommand
import ru.loginov.serbian.bot.telegram.command.impl.permission.PermissionBotCommand

@Component
@SubCommand(parents = [PermissionBotCommand::class])
@RequiredPermission("commands.permission.users")
class SubCommandUsersForPermission : ComplexSubCommand() {
    override val commandName: String = "users"
    override val shortDescription: String = "@{bot.command.permissions.users._shortDescription}"
}