package ru.loginov.serbian.bot.telegram.command.context.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.data.manager.permission.PermissionManager
import ru.loginov.serbian.bot.data.manager.user.UserManager
import ru.loginov.serbian.bot.telegram.callback.TelegramCallbackManager
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContextFactory
import ru.loginov.telegram.api.TelegramAPI

@Component
class DefaultBotCommandExecuteContextFactory : BotCommandExecuteContextFactory {

    @Autowired
    private lateinit var telegramApi: TelegramAPI

    @Autowired
    private lateinit var userManager: UserManager

    @Autowired
    private lateinit var callbackManager: TelegramCallbackManager

    @Autowired
    private lateinit var permissionManager: PermissionManager

    @Autowired
    private lateinit var localizationManager: LocalizationManager

    override fun createContext(
            userId: Long,
            charId: Long,
            lang: String?,
            argumentsStr: String
    ): BotCommandExecuteContext {
        val user = userManager.getUser(userId)
                ?: userManager.createUser(userId, charId, lang)
                ?: error("Can not save user")

        if (user.language == null) {
            user.language = lang
        }

        return DefaultBotCommandExecuteContext(
                charId,
                user,
                telegramApi,
                permissionManager,
                localizationManager,
                callbackManager,
                argumentsStr
        )
    }

}

