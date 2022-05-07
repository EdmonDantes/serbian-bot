package ru.loginov.serbian.bot.telegram.command.impl.category

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.BotCommand
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
import ru.loginov.telegram.api.util.markdown2

@Component
class CategoryBotCommand : ComplexBotCommand() {
    override val commandName: String = "category"
    override val shortDescription: String = "Work with categories"
    override val description: StringBuilderMarkdownV2? = markdown2 {
        append("Work with categories")
    }
}