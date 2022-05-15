package ru.loginov.serbian.bot.telegram.command.context

import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.spring.localization.context.LocalizationContext
import ru.loginov.serbian.bot.spring.permission.PermissionContext
import ru.loginov.serbian.bot.telegram.command.context.arguments.BotCommandArgumentManager
import ru.loginov.telegram.api.TelegramAPI

interface BotCommandExecuteContext : PermissionContext, LocalizationContext, TelegramAPI {
    val telegram: TelegramAPI // FIXME: Should be removed
    val user: UserDto
    val chatId: Long
    val argumentManager: BotCommandArgumentManager // FIXME: Should be implemented in context
}