//package ru.loginov.serbian.bot.telegram.command.impl.permission.groups.permissions
//
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Component
//import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
//import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.withLocalization
//import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
//import ru.loginov.serbian.bot.telegram.command.base.AbstractSubCommand
//import ru.loginov.serbian.bot.util.markdown2
//import ru.loginov.simple.permissions.annotation.RequiredPermission
//import ru.loginov.simple.permissions.manager.PermissionManager
//
//@Component
//@SubCommand(parents = [SubCommandPermissionsForGroup::class])
//@RequiredPermission("commands.permission.groups.permissions.delete")
//class SubCommandDeleteForPermissions : AbstractSubCommand() {
//
//    @Autowired
//    private lateinit var permissionManager: PermissionManager
//
//    override val commandName: String = "delete"
//    override val actionDescription: String = "@{bot.command.permissions.groups.permissions.delete._shortDescription}"
//
//    override suspend fun execute(context: BotCommandExecuteContext) {
//        context.withLocalization("bot.command.permissions.groups.permissions.delete._argument") {
//            val groupName = argument("groupName", "group")
//                    .required()
//                    .validate { permissionManager.hasGroup(it) }
//                    .get()
//
//            val permission = argument("permission", "permission")
//                    .required()
//                    .validate { permissionManager.hasPermission(it) }
//                    .get()
//
//
//            try {
//                if (permissionManager.deletePermissionForGroup(groupName, permission)) {
//                    context.sendMessage {
//                        markdown2(context) {
//                            append("@{bot.command.permissions.groups.permissions.delete._.success}{$permission}{$groupName}")
//                        }
//                    }
//                    return
//                }
//            } catch (e: Exception) {
//                LOGGER.error("Can not delete permission '$permission' to group with name '$groupName'", e)
//            }
//
//            context.sendMessage {
//                markdown2(context) {
//                    append("@{bot.command.permissions.groups.permissions.delete._.can.not.delete.permission}{$permission}{$groupName}")
//                }
//            }
//        }
//    }
//
//    companion object {
//        private val LOGGER = LoggerFactory.getLogger(SubCommandDeleteForPermissions::class.java)
//    }
//}