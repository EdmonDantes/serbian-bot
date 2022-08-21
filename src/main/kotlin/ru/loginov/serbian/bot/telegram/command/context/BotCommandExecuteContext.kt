package ru.loginov.serbian.bot.telegram.command.context

import io.github.edmondantes.simple.localization.LocalizationRequest
import io.github.edmondantes.simple.localization.context.LocalizationContext
import io.github.edmondantes.simple.permissions.PermissionOwner
import ru.loginov.serbian.bot.data.dto.user.UserDescription
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.Message

interface BotCommandExecuteContext : PermissionOwner, TelegramAPI {

    val arguments: ArgumentManager<LocalizationRequest>
    val localization: LocalizationContext

    val chatId: Long
    val user: UserDescription
    val inputMessage: Message
}