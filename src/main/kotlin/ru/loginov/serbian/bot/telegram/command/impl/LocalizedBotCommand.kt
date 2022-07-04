package ru.loginov.serbian.bot.telegram.command.impl

import ru.loginov.serbian.bot.telegram.command.argument.manager.LocalizationArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.withPrefix
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.context.impl.ParentBotCommandExecuteContext
import ru.loginov.simple.localization.context.LocalizationContext
import ru.loginov.simple.localization.context.impl.withPrefix
import ru.loginov.simple.localization.impl.localizationKey
import ru.loginov.simple.permissions.annotation.ForcePermissionCheck

@ForcePermissionCheck
abstract class LocalizedBotCommand(
        localizedPrefix: String,
        private val enabledActionDescription: Boolean = true,
        enabledDescription: Boolean = false,
) : AbstractBotCommand() {

    private val localizedPrefix = if (localizedPrefix.isEmpty()) "" else if (localizedPrefix.endsWith('.')) localizedPrefix else "$localizedPrefix."

    override fun getActionDescription(context: BotCommandExecuteContext): String? =
            (if (!enabledActionDescription)
                null
            else
                context.localization.localizeOrNull(localizationKey("${localizedPrefix}_actionDescription")))
                    ?: super.getActionDescription(context)


    override suspend fun execute(context: BotCommandExecuteContext) {
        val localizedContext = object : ParentBotCommandExecuteContext(context) {
            override val localization: LocalizationContext =
                    parent.localization.withPrefix(localizedPrefix)
            override val arguments: LocalizationArgumentManager =
                    parent.arguments.withPrefix(localizedPrefix + "_argument")

        }

        localizedContext.action()
    }

    protected open suspend fun BotCommandExecuteContext.action() {}
}