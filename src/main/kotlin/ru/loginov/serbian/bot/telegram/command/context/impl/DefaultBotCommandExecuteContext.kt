package ru.loginov.serbian.bot.telegram.command.context.impl

import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.data.manager.permission.PermissionManager
import ru.loginov.serbian.bot.telegram.command.context.arguments.BotCommandArgumentManager
import ru.loginov.telegram.api.TelegramAPI

class DefaultBotCommandExecuteContext(
        override val chatId: Long,
        override val user: UserDto,
        telegram: TelegramAPI,
        permissionManager: PermissionManager,
        localizationManager: LocalizationManager,
        override val argumentManager: BotCommandArgumentManager
) : AbstractBotCommandExecuteContext(telegram, permissionManager, localizationManager)