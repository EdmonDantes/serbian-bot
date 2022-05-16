package ru.loginov.serbian.bot.telegram.command.impl.permission.groups

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.permission.PermissionManager
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand(parents = [SubCommandGroupsForPermissions::class])
@RequiredPermission("commands.permission.groups.delete")
class SubCommandDeleteForGroups : AbstractSubCommand() {

    @Autowired
    private lateinit var permissionManager: PermissionManager

    override val commandName: String = "delete"
    override val shortDescription: String = "@{bot.command.permissions.groups.delete._shortDescription=}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val groupName = context.getNextArgument("@{bot.command.permissions.groups.delete._argument.groupName}")
        if (groupName.isNullOrEmpty()) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.permissions.groups.delete._.can.not.delete.group.without.name}")
                }
            }
        }

        val groupForReplace = context.getNextArgument(
                "@{bot.command.permissions.groups.delete._argument.groupForReplace}",
                true
        )

        try {
            if (permissionManager.deleteGroup(groupName!!, groupForReplace)) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.permissions.groups.delete._.success}{$groupName}")
                    }
                }
                return
            }
        } catch (e: Exception) {
            LOGGER.error("Can not delete group with name '$groupName'", e)
        }
        context.sendMessage {
            markdown2(context) {
                append("@{bot.command.permissions.groups.delete._.can.not.delete.group}{$groupName}")
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandDeleteForGroups::class.java)
    }
}