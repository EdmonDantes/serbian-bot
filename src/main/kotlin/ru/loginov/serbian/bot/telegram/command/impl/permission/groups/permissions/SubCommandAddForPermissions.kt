package ru.loginov.serbian.bot.telegram.command.impl.permission.groups.permissions

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.permission.PermissionManager
import ru.loginov.serbian.bot.data.manager.permission.tree.PermissionTree
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.context.getNextArgument
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand(parents = [SubCommandPermissionsForGroup::class])
@RequiredPermission("commands.permission.groups.permissions.add")
class SubCommandAddForPermissions : AbstractSubCommand() {

    @Autowired
    private lateinit var permissionManager: PermissionManager

    override val commandName: String = "add"
    override val shortDescription: String = "@{bot.command.permissions.groups.permissions.add._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val groupName = context.getNextArgument(
                "@{bot.command.permissions.groups.permissions.add._argument.group}",
                { "@{bot.command.permissions.groups.permissions.add._argument.groupName.not.valid}{$it}" }
        ) { it != null && permissionManager.hasGroup(it) }
                ?: error("Group name can not be null")

        val permission = context.getNextArgument(
                "@{bot.command.permissions.groups.permissions.add._argument.permission}",
                { "@{bot.command.permissions.groups.permissions.add._argument.permission.not.valid}{$it}" }
        ) { it != null && PermissionTree.isValidPermission(it) }
                ?: error("Permission can not be null")

        try {
            if (permissionManager.addPermissionForGroup(groupName, permission)) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.permissions.groups.permissions.add._.success}{$permission}{$groupName}")
                    }
                }
                return
            }
        } catch (e: Exception) {
            LOGGER.error("Can not add permission '$permission' to group with name '$groupName'", e)
        }

        context.sendMessage {
            markdown2(context) {
                append("@{bot.command.permissions.groups.permissions.add._.can.not.add.permission}{$permission}{$groupName}")
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandAddForPermissions::class.java)
    }
}