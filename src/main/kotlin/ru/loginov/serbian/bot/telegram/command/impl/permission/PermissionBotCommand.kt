package ru.loginov.serbian.bot.telegram.command.impl.permission

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand
import ru.loginov.simple.permissions.annotation.RequiredPermission

@Component
@RequiredPermission("commands.permission")
class PermissionBotCommand : ComplexBotCommand() {
    override val commandName: String = "permission"
    override val actionDescription: String = "@{bot.command.permissions._shortDescription}"
}