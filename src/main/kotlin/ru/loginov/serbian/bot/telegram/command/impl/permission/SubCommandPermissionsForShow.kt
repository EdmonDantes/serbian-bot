package ru.loginov.serbian.bot.telegram.command.impl.permission

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.permission.PermissionManager
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand

@Component
@SubCommand(parents = [SubCommandShowForPermission::class])
@RequiredPermission("commands.permission.show.permissions")
class SubCommandPermissionsForShow : AbstractSubCommand() {

    @Autowired
    private lateinit var permissionManager: PermissionManager

    override val commandName: String = "permissions"
    override val shortDescription: String = "Show all registered permissions"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.sendMessageWithoutLimit {
            buildText {
                append("All registered permissions:")
                permissionManager.getAllPermissions().forEach {
                    append('\n')
                    append(it)
                }
            }
        }
    }
}