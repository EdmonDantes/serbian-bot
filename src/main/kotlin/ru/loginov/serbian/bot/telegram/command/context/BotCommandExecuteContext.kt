package ru.loginov.serbian.bot.telegram.command.context

import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.spring.localization.context.LocalizationContext
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.simple.permissions.PermissionContext
import ru.loginov.telegram.api.TelegramAPI

// WARN: Use inheritance instead of composition for reduce count of creating objects for every command call
interface BotCommandExecuteContext : PermissionContext, LocalizationContext, ArgumentManager, TelegramAPI {
    val user: UserDto
    val chatId: Long
}