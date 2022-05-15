package ru.loginov.serbian.bot.telegram.command.impl.permission

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.permission.PermissionManager
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand

@Component
@SubCommand(parents = [SubCommandGroupsForPermissions::class])
@RequiredPermission("commands.permission.groups.delete")
class SubCommandDeleteForGroups : AbstractSubCommand() {

    @Autowired
    private lateinit var permissionManager: PermissionManager

    override val commandName: String = "delete"
    override val shortDescription: String = "Delete permission`s group"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val groupName = context.argumentManager.getNextArgument("Please write group name which will be delete")
        if (groupName.isNullOrEmpty()) {
            context.sendMessage {
                buildText {
                    append("Can not delete group without name")
                }
            }
        }

        val groupForReplace = context.argumentManager.getNextArgument("Please write group name for replace", true)

        try {
            if (permissionManager.deleteGroup(groupName!!, groupForReplace)) {
                context.sendMessage {
                    buildText {
                        append("Successfully delete group with name '$groupName'")
                    }
                }
                return
            }
        } catch (e: Exception) {
            LOGGER.error("Can not delete group with name '$groupName'", e)
        }

        context.sendMessage {
            buildText {
                append("Can not delete group with name '$groupName'")
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandDeleteForGroups::class.java)
    }
}