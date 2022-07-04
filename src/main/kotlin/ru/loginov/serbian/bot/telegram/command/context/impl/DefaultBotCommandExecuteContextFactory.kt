package ru.loginov.serbian.bot.telegram.command.context.impl

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.data.manager.user.UserManager
import ru.loginov.serbian.bot.data.manager.user.UserSettingsManager
import ru.loginov.serbian.bot.telegram.callback.TelegramCallbackManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.EmptyArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.StringArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.TelegramArgumentManager
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContextFactory
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext
import ru.loginov.simple.localization.context.impl.DefaultLocalizationContext
import ru.loginov.simple.localization.manager.LocalizationManager
import ru.loginov.simple.permissions.manager.PermissionManager
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.Chat
import ru.loginov.telegram.api.entity.Message
import javax.annotation.PostConstruct

@Component
class DefaultBotCommandExecuteContextFactory(
        private val telegramApi: TelegramAPI,
        private val userManager: UserManager,
        private val callbackManager: TelegramCallbackManager,
        private val permissionManager: PermissionManager,
        private val localizationManager: LocalizationManager,
        private val userSettingsManager: UserSettingsManager
) : BotCommandExecuteContextFactory {

    private val localizationContexts = HashMap<String, LocalizationContext>()

    @PostConstruct
    fun init() {
        localizationManager.supportLanguages.forEach {
            localizationContexts[it] = DefaultLocalizationContext(it, localizationManager)
        }
    }

    override fun createContext(
            userId: Long,
            chatId: Long,
            incomeMessage: Message,
            argumentsStr: String,
            lang: String?
    ): BotCommandExecuteContext {
        val user = userManager.findByIdWithData(userId, userSettingsManager.possibleSettings)
                ?: userManager.create(userId, chatId, lang)
                ?: error("Can not save user with id '$userId'")

        val contextLanguage = user.language ?: localizationManager.defaultLanguage
        val localizationContext = localizationContexts[contextLanguage]
                ?: error("Can not get localization context for language '$contextLanguage'")

        val argumentManager = StringArgumentManager(
                TelegramArgumentManager(
                        telegramApi,
                        callbackManager,
                        localizationContext,
                        localizationManager,
                        chatId,
                        userId
                ), argumentsStr
        )

        return DefaultBotCommandExecuteContext(
                telegramApi,
                permissionManager,
                localizationContext,
                argumentManager,
                chatId,
                user,
                incomeMessage
        )
    }

    override fun createEmptyContext(lang: String?): BotCommandExecuteContext {

        val contextLanguage = lang ?: localizationManager.defaultLanguage
        val localizationContext = localizationContexts[contextLanguage]
                ?: error("Can not get localization context for language '$contextLanguage'")

        return object : AbstractBotCommandExecuteContext(
                telegramApi,
                permissionManager,
                localizationContext,
                EmptyArgumentManager() as ArgumentManager<LocalizationRequest>,
                -1,
                UserDto().also { it.language = lang },
                Message(-1, chat = Chat(-1, "private", firstName = ""), sendDateTime = 0, text = "")
        ) {
            override fun hasPermission(permission: String): Boolean = false
            override fun hasAllPermissions(permissions: List<String>): Boolean = false
        }
    }
}

