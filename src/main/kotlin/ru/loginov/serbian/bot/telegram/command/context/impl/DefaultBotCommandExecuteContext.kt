package ru.loginov.serbian.bot.telegram.command.context.impl

import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.data.manager.permission.PermissionManager
import ru.loginov.serbian.bot.telegram.command.context.arguments.BotCommandArgumentManager
import ru.loginov.telegram.api.TelegramAPI

class DefaultBotCommandExecuteContext(
        telegram: TelegramAPI,
        permissionManager: PermissionManager,
        override val user: UserDto,
        override val chatId: Long,
        override val argumentManager: BotCommandArgumentManager
) : AbstractBotCommandExecuteContext(telegram, permissionManager)