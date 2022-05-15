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
@RequiredPermission("commands.permission.groups.create")
class SubCommandCreateForGroups : AbstractSubCommand() {

    @Autowired
    private lateinit var permissionManager: PermissionManager

    override val commandName: String = "create"
    override val shortDescription: String = "Create new permission`s group"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val groupName = context.argumentManager.getNextArgument("Group name")
        if (groupName.isNullOrEmpty()) {
            context.sendMessage {
                buildText {
                    bold {
                        append("ERROR:\nCan not create group without name")
                    }
                }
            }
        }
        try {
            permissionManager.createGroup(groupName!!.lowercase())
            context.sendMessage {
                buildText {
                    append("Successfully created new group with name '${groupName.lowercase()}'")
                }
            }
        } catch (e: Exception) {
            LOGGER.error("Can not create new group with name '${groupName?.lowercase()}'", e)

            context.sendMessage {
                buildText {
                    append("Can not create new group with name '$groupName'. Internal error.")
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandCreateForGroups::class.java)
    }
}