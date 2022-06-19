package ru.loginov.serbian.bot.telegram.command.impl.permission.users

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.user.UserManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.withLocalization
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.simple.permissions.annotation.RequiredPermission
import ru.loginov.simple.permissions.manager.PermissionManager

@Component
@SubCommand(parents = [SubCommandUsersForPermission::class])
@RequiredPermission("commands.permission.users.setgroup")
class SubCommandSetGroupForUsers : AbstractSubCommand() {
    @Autowired
    private lateinit var userManager: UserManager

    @Autowired
    private lateinit var permissionManager: PermissionManager

    override val commandName: String = "setgroup"
    override val shortDescription: String = "@{bot.command.permissions.users.setgroup._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.withLocalization("bot.command.permissions.users.setgroup._argument") {
            val userId = argument("userId", "userId")
                    .required()
                    .transform { it.toLongOrNull() }
                    .validate { it != null }
                    .get()!!

            val groupName = argument("groupName", "groupName")
                    .required()
                    .validate { permissionManager.hasGroup(it) }
                    .get()

            userManager.update(userId, permissionGroup = groupName)
        }

    }
}