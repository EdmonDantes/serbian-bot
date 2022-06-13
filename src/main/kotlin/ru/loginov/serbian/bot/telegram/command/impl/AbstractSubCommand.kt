package ru.loginov.serbian.bot.telegram.command.impl

import ru.loginov.serbian.bot.telegram.command.SubCommand
import ru.loginov.simple.permissions.annotation.ForcePermissionCheck

@ForcePermissionCheck
abstract class AbstractSubCommand : AbstractBotCommand(), SubCommand {}