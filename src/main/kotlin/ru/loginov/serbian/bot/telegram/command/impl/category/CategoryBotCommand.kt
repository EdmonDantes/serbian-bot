package ru.loginov.serbian.bot.telegram.command.impl.category

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand
import ru.loginov.simple.localization.impl.localizationKey

@Component
//TODO: Add in next version. After update permission manager. @RequiredPermission("commands.category")
class CategoryBotCommand : ComplexBotCommand() {
    override val commandName: String = "category"
    override fun getActionDescription(context: BotCommandExecuteContext): String? =
            context.localization.localize(localizationKey("bot.command.category._actionDescription"))
}