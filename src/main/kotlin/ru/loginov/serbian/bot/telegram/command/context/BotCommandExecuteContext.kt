package ru.loginov.serbian.bot.telegram.command.context

import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext
import ru.loginov.simple.permissions.PermissionContext
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.Message

interface BotCommandExecuteContext : PermissionContext, TelegramAPI {

    val arguments: ArgumentManager<LocalizationRequest>
    val localization: LocalizationContext

    val chatId: Long
    val user: UserDto
    val inputMessage: Message
}