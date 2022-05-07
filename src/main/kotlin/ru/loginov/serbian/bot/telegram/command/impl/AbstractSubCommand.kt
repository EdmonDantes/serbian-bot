package ru.loginov.serbian.bot.telegram.command.impl

import ru.loginov.serbian.bot.spring.permission.annotation.PermissionCheck
import ru.loginov.serbian.bot.telegram.command.SubCommand

@PermissionCheck
abstract class AbstractSubCommand : AbstractBotCommand(), SubCommand {}