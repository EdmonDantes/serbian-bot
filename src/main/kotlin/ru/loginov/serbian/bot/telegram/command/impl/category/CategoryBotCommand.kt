package ru.loginov.serbian.bot.telegram.command.impl.category

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand

@Component
//TODO: Add in next version. After update permission manager. @RequiredPermission("commands.category")
class CategoryBotCommand : ComplexBotCommand() {
    override val commandName: String = "category"
    override val shortDescription: String = "@{bot.command.category._shortDescription}"
}