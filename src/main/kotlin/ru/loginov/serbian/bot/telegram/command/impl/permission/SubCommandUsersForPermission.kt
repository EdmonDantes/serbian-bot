package ru.loginov.serbian.bot.telegram.command.impl.permission

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.SubCommand
import ru.loginov.serbian.bot.telegram.command.impl.ComplexSubCommand

@Component
@SubCommand(parents = [PermissionBotCommand::class])
@RequiredPermission("commands.permission.users")
class SubCommandUsersForPermission : ComplexSubCommand() {
    override val commandName: String = "users"
    override val shortDescription: String = "Manage users permissions"
}