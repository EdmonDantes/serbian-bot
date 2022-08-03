package ru.loginov.serbian.bot.telegram.command.impl.permission

import io.github.edmondantes.simple.localization.impl.localizationKey
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.base.ComplexBotCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.simple.permissions.annotation.RequiredPermission

@Component
@RequiredPermission("commands.permission")
class PermissionBotCommand : ComplexBotCommand() {
    override val commandName: String = "permission"
    override fun getActionDescription(context: BotCommandExecuteContext): String? =
            context.localization.localize(localizationKey("bot.command.permissions._actionDescription"))
}