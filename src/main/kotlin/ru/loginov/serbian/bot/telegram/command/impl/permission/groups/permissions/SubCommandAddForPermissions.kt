//package ru.loginov.serbian.bot.telegram.command.impl.permission.groups.permissions
//
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Component
//import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
//import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.withLocalization
//import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
//import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
//import ru.loginov.serbian.bot.util.markdown2
//import ru.loginov.simple.permissions.annotation.RequiredPermission
//import ru.loginov.simple.permissions.manager.PermissionManager
//
//@Component
//@SubCommand(parents = [SubCommandPermissionsForGroup::class])
//@RequiredPermission("commands.permission.groups.permissions.add")
//class SubCommandAddForPermissions(
//        private val permissionManager: PermissionManager
//) : AbstractSubCommand() {
//
//
//    override val commandName: String = "add"
//    override val actionDescription: String = "@{bot.command.permissions.groups.permissions.add._shortDescription}"
//
//    override suspend fun execute(context: BotCommandExecuteContext) {
//        context.withLocalization("bot.command.permissions.groups.permissions.add._argument") {
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
//            try {
//                if (permissionManager.addPermissionForGroup(groupName, permission)) {
//                    context.sendMessage {
//                        markdown2(context) {
//                            append("@{bot.command.permissions.groups.permissions.add._.success}{$permission}{$groupName}")
//                        }
//                    }
//                    return
//                }
//            } catch (e: Exception) {
//                LOGGER.error("Can not add permission '$permission' to group with name '$groupName'", e)
//            }
//
//            context.sendMessage {
//                markdown2(context) {
//                    append("@{bot.command.permissions.groups.permissions.add._.can.not.add.permission}{$permission}{$groupName}")
//                }
//            }
//        }
//    }
//
//    companion object {
//        private val LOGGER = LoggerFactory.getLogger(SubCommandAddForPermissions::class.java)
//    }
//}