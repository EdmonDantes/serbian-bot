package ru.loginov.serbian.bot.telegram.command.impl.permission.groups.permissions

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.impl.ComplexSubCommand
import ru.loginov.serbian.bot.telegram.command.impl.permission.groups.SubCommandGroupsForPermissions
import ru.loginov.simple.permissions.annotation.RequiredPermission

@Component
@SubCommand(parents = [SubCommandGroupsForPermissions::class])
@RequiredPermission("commands.permission.groups.permissions")
class SubCommandPermissionsForGroup : ComplexSubCommand() {
    override val commandName: String = "permissions"
    override val actionDescription: String = "@{bot.command.permissions.groups.permissions._shortDescription}"
}