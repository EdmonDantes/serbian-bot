package ru.loginov.serbian.bot.telegram.command.impl.permission.users

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.permission.PermissionManager
import ru.loginov.serbian.bot.data.manager.user.UserManager
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.context.getNextArgument
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand

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
        val userId = context.getNextArgument("@{bot.command.permissions.users.setgroup._argument.userId}")
                ?.toLongOrNull()
                ?: error("User id should be number and not null")

        val group = context.getNextArgument(
                "@{bot.command.permissions.users.setgroup._argument.groupName}",
                { "@{bot.command.permissions.users.setgroup._.can.not.find.group}{$it}" }
        ) { it == null || permissionManager.hasGroup(it) }

        userManager.updatePermissionGroup(userId, group ?: "")
    }
}