package ru.loginov.serbian.bot.telegram.command.impl.permission.groups.permissions

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2
import ru.loginov.simple.permissions.annotation.RequiredPermission
import ru.loginov.simple.permissions.manager.PermissionManager

@Component
@SubCommand(parents = [SubCommandPermissionsForGroup::class])
@RequiredPermission("commands.permission.groups.permissions.show")
class SubCommandShowForPermissions : AbstractSubCommand() {

    @Autowired
    private lateinit var permissionManager: PermissionManager

    override val commandName: String = "show"
    override val shortDescription: String = "@{bot.command.permissions.groups.permissions.show._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {

        val groupName = context.argument(
                "groupName",
                "@{bot.command.permissions.groups.permissions.show._argument.group}"
        )
                .required()
                .validate { permissionManager.hasGroup(it) }
                .get()


        try {
            val owner = permissionManager.getOwnerForGroupOrDefault(groupName)
                    ?: error("Permission owner for group with name '$groupName' can not be null")
            val grantedPermissions = permissionManager.permissions.filter { owner.checkPermission(it) }

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