package ru.loginov.serbian.bot.telegram.command.impl.permission.groups

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.impl.ComplexSubCommand
import ru.loginov.serbian.bot.telegram.command.impl.permission.PermissionBotCommand
import ru.loginov.simple.permissions.annotation.RequiredPermission

@Component
@SubCommand(parents = [PermissionBotCommand::class])
@RequiredPermission("commands.permission.groups")
class SubCommandGroupsForPermissions : ComplexSubCommand() {
    override val commandName: String = "groups"
    override val actionDescription: String = "@{bot.command.permissions.groups._shortDescription}"
}