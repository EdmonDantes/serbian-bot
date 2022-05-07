package ru.loginov.serbian.bot.telegram.command.impl.permission

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand

@Component
@RequiredPermission("commands.permission")
class PermissionBotCommand : ComplexBotCommand() {
    override val commandName: String = "permission"
}