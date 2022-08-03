//package ru.loginov.serbian.bot.telegram.command.impl.permission.show
//
//import org.springframework.stereotype.Component
//import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
//import ru.loginov.serbian.bot.telegram.command.base.ComplexSubCommand
//import ru.loginov.serbian.bot.telegram.command.impl.permission.PermissionBotCommand
//import ru.loginov.simple.permissions.annotation.RequiredPermission
//
//@Component
//@SubCommand(parents = [PermissionBotCommand::class])
//@RequiredPermission("commands.permission.show")
//class SubCommandShowForPermission : ComplexSubCommand() {
//    override val commandName: String = "show"
//    override val actionDescription: String = "Show groups or permissions"
//}