package ru.loginov.serbian.bot.telegram.command.impl.category

import io.github.edmondantes.simple.localization.impl.localizationKey
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.base.ComplexBotCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext

@Component
//TODO: Add in next version. After update permission manager. @RequiredPermission("commands.category")
class CategoryBotCommand : ComplexBotCommand() {
    override val commandName: String = "category"
    override fun getActionDescription(context: BotCommandExecuteContext): String? =
            context.localization.localize(localizationKey("bot.command.category._actionDescription"))
}