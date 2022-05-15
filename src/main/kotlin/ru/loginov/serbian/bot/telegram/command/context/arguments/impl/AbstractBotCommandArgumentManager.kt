package ru.loginov.serbian.bot.telegram.command.context.arguments.impl

import ru.loginov.serbian.bot.spring.localization.annotation.Localized
import ru.loginov.serbian.bot.spring.localization.context.LocalizationContext
import ru.loginov.serbian.bot.telegram.command.context.arguments.BotCommandArgumentManager

@Localized
abstract class AbstractBotCommandArgumentManager : BotCommandArgumentManager {

    override suspend fun getNextArgument(
            localizationString: String?,
            context: LocalizationContext,
            optional: Boolean
    ): String? =
            getNextArgument(localizationString, optional)
}