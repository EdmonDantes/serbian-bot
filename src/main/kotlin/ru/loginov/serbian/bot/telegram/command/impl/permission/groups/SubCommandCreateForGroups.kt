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
@RequiredPermission("commands.permission.groups.create")
class SubCommandCreateForGroups : AbstractSubCommand() {

    @Autowired
    private lateinit var permissionManager: PermissionManager

    override val commandName: String = "create"
    override val shortDescription: String = "@{bot.command.permissions.groups.create._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val groupName = context.getNextArgument("@{bot.command.permissions.groups.create._argument.groupName}")
        if (groupName.isNullOrEmpty()) {
            context.sendMessage {
                markdown2(context) {
                    bold {
                        append("@{bot.command.permissions.groups.create._.can.not.create.group.without.name}")
                    }
                }
            }
        }

        // TODO: Add message when group already exists
        try {
            if (permissionManager.createGroup(groupName!!.lowercase())) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.permissions.groups.create._.success}{${groupName.lowercase()}}")
                    }
                }
                return
            }
        } catch (e: Exception) {
            LOGGER.error("Can not create new group with name '$groupName'", e)
        }

        context.sendMessage {
            markdown2(context) {
                append("@{bot.command.permissions.groups.create._.can.not.create.group}{$groupName}. @{phases.internal.error}.")
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandDeleteForGroups::class.java)
    }
}