package ru.loginov.serbian.bot.telegram.command.impl.permission.show

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.telegram.command.impl.permission.groups.SubCommandGroupsForPermissions
import ru.loginov.simple.permissions.annotation.RequiredPermission
import ru.loginov.simple.permissions.manager.PermissionManager

@Component
@SubCommand(parents = [SubCommandShowForPermission::class, SubCommandGroupsForPermissions::class])
@RequiredPermission("commands.permission.show.groups")
class SubCommandGroupsForShow : AbstractSubCommand() {

    @Autowired
    private lateinit var permissionManager: PermissionManager

    override val commandName: String = "groups"
    override val actionDescription: String = "Show all permissions groups"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.sendMessageWithoutLimit {
            markdown2 {
                append("All saved groups:")
                permissionManager.groups.forEach {
                    append('\n')
                    append(it)
                }
            }
        }
    }
}