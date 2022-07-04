package ru.loginov.serbian.bot.telegram.command.context.impl

import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.withLocalization
import ru.loginov.simple.localization.context.LocalizationContext
import ru.loginov.simple.permissions.manager.PermissionManager
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.Message

class DefaultBotCommandExecuteContext(
        telegram: TelegramAPI,
        permissionManager: PermissionManager,
        localizationContext: LocalizationContext,
        arguments: ArgumentManager<String>,
        chatId: Long,
        user: UserDto,
        incomeMessage: Message
) : AbstractBotCommandExecuteContext(
        telegram,
        permissionManager,
        localizationContext,
        arguments.withLocalization(localizationContext),
        chatId,
        user,
        incomeMessage
)