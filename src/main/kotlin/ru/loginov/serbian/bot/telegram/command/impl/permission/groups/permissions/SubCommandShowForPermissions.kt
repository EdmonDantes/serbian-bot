package ru.loginov.serbian.bot.telegram.command.impl.permission.groups.permissions

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.permission.PermissionManager
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.context.getNextArgument
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand(parents = [SubCommandPermissionsForGroup::class])
@RequiredPermission("commands.permission.groups.permissions.show")
class SubCommandShowForPermissions : AbstractSubCommand() {

    @Autowired
    private lateinit var permissionManager: PermissionManager

    override val commandName: String = "show"
    override val shortDescription: String = "@{bot.command.permissions.groups.permissions.show._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val groupName = context.getNextArgument(
                "@{bot.command.permissions.groups.permissions.show._argument.group}",
                { "@{bot.command.permissions.groups.permissions.show._.can.not.find.group}{$it}" }
        ) { it != null && permissionManager.hasGroup(it) }
                ?: error("Group name can not be null")

        try {
            val owner = permissionManager.getPermissionsForGroup(groupName)
                    ?: error("Permission owner for group with name '$groupName' can not be null")
            val grantedPermissions = permissionManager.permissions.filter { owner.havePermission(it) }

            context.sendMessageWithoutLimit {
                markdown2(context) {
                    append("@{bot.command.permissions.groups.permissions.add._.success}{$groupName}\n")
                    grantedPermissions.forEach {
                        append("\n$it")
                    }
                }
            }
        } catch (e: Exception) {
            LOGGER.error("Can not show permissions for group with name '$groupName'", e)
            throw e
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandShowForPermissions::class.java)
    }
}