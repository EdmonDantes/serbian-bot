package ru.loginov.serbian.bot.telegram.command.context.impl

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.data.manager.permission.PermissionManager
import ru.loginov.serbian.bot.data.manager.user.UserManager
import ru.loginov.serbian.bot.telegram.callback.TelegramCallbackManager
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContextFactory
import ru.loginov.telegram.api.TelegramAPI

@Component
class DefaultBotCommandExecuteContextFactory(
        private val telegramApi: TelegramAPI,
        private val userManager: UserManager,
        private val callbackManager: TelegramCallbackManager,
        private val permissionManager: PermissionManager,
        private val localizationManager: LocalizationManager
) : BotCommandExecuteContextFactory {

    override fun createContext(
            userId: Long,
            chatId: Long,
            lang: String?,
            argumentsStr: String
    ): BotCommandExecuteContext {
        val user = userManager.findById(userId)
                ?: userManager.create(userId, chatId, lang)
                ?: error("Can not save user")

        if (user.language == null) {
            user.language = lang
        }

        if (user.language == null || !localizationManager.isSupport(user.language!!)) {
            user.language = localizationManager.defaultLanguage
        }

        return DefaultBotCommandExecuteContext(
                chatId,
                user,
                telegramApi,
                permissionManager,
                localizationManager,
                callbackManager,
                argumentsStr
        )
    }

    override fun createEmptyContext(lang: String?): BotCommandExecuteContext {
        return object : AbstractBotCommandExecuteContext(
                telegramApi,
                permissionManager,
                localizationManager,
                callbackManager,
                ""
        ) {
            override val user: UserDto = UserDto().also { it.language = lang }
            override val chatId: Long = -1
            override fun havePermission(permission: String): Boolean = false
            override fun haveAllPermissions(permissions: List<String>): Boolean = false
        }
    }
}

