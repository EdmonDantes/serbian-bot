package ru.loginov.serbian.bot.telegram.command.impl.permission

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.permission.PermissionManager
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand

@Component
@SubCommand(parent = SubCommandShowForPermission::class, subCommandName = "groups")
@RequiredPermission("commands.permission.show.groups")
class SubCommandGroupsForShow : AbstractSubCommand() {

    @Autowired
    private lateinit var permissionManager: PermissionManager

    override val commandName: String = "groups"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.sendMessageWithoutLimit {
            buildText {
                append("All saved groups:")
                permissionManager.getAllGroups().forEach {
                    if (it.name != null) {
                        append('\n')
                        append(it.name!!)
                    }
                }
            }
        }
    }
}