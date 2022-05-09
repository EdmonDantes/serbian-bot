package ru.loginov.serbian.bot.telegram.command.impl.permission

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
import ru.loginov.telegram.api.util.markdown2FromString

@Component
@RequiredPermission("commands.permission")
class PermissionBotCommand : ComplexBotCommand() {
    override val commandName: String = "permission"
    override val shortDescription: String = "Command help to manage permissions and permission groups"
    override val description: StringBuilderMarkdownV2 = markdown2FromString(shortDescription)
}