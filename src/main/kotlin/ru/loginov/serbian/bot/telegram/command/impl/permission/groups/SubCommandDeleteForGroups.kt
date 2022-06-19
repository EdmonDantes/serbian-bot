package ru.loginov.serbian.bot.telegram.command.impl.permission.groups

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.withLocalization
import ru.loginov.serbian.bot.telegram.command.argument.optionalAndGet
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2
import ru.loginov.simple.permissions.annotation.RequiredPermission
import ru.loginov.simple.permissions.manager.PermissionManager

@Component
@SubCommand(parents = [SubCommandGroupsForPermissions::class])
@RequiredPermission("commands.permission.groups.delete")
class SubCommandDeleteForGroups : AbstractSubCommand() {

    @Autowired
    private lateinit var permissionManager: PermissionManager

    override val commandName: String = "delete"
    override val shortDescription: String = "@{bot.command.permissions.groups.delete._shortDescription=}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.withLocalization("bot.command.permissions.groups.delete._argument") {
            val groupName = argument("groupName", "groupName").requiredAndGet()
            val groupForReplace = argument("groupForReplace", "groupForReplace").optionalAndGet()

            try {
                if (permissionManager.deleteGroup(groupName, groupForReplace)) {
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
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandDeleteForGroups::class.java)
    }
}