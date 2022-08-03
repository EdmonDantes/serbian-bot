//package ru.loginov.serbian.bot.telegram.command.impl.permission.show
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Component
//import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
//import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
//import ru.loginov.serbian.bot.telegram.command.base.AbstractSubCommand
//import ru.loginov.simple.permissions.annotation.RequiredPermission
//import ru.loginov.simple.permissions.manager.PermissionManager
//
//@Component
//@SubCommand(parents = [SubCommandShowForPermission::class])
//@RequiredPermission("commands.permission.show.permissions")
//class SubCommandPermissionsForShow : AbstractSubCommand() {
//
//    @Autowired
//    private lateinit var permissionManager: PermissionManager
//
//    override val commandName: String = "permissions"
//    override val actionDescription: String = "Show all registered permissions"
//
//    override suspend fun execute(context: BotCommandExecuteContext) {
//        context.sendMessageWithoutLimit {
//            markdown2 {
//                append("All registered permissions:")
//                permissionManager.permissions.forEach {
//                    append('\n')
//                    append(it)
//                }
//            }
//        }
//    }
//}