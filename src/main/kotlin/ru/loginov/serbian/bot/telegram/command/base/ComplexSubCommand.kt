package ru.loginov.serbian.bot.telegram.command.base

import ru.loginov.serbian.bot.telegram.command.SubCommand
import ru.loginov.simple.permissions.annotation.ForcePermissionCheck

@ForcePermissionCheck
abstract class ComplexSubCommand : ComplexBotCommand(), SubCommand {
}