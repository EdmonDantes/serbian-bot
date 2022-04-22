package ru.loginov.serbian.bot.telegram.service.command.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.telegram.service.command.BotCommand
import ru.loginov.serbian.bot.telegram.service.command.context.BotCommandExecuteContext
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
import ru.loginov.telegram.api.util.markdown2FromString

@Component
class GetAllCategoriesCommand : BotCommand {

    @Autowired
    private lateinit var categoryManager: CategoryManager

    override val commandName: String = "categories"
    override val description: StringBuilderMarkdownV2 = markdown2FromString("Print all available categories")
    override val usage: StringBuilderMarkdownV2? = null

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.telegramApi.sendMessage {
            chatId = context.chatId
            buildText {
                append("Available categories:")
                categoryManager.getAllCategories(context.lang ?: "en").forEach {
                    append("\n")
                    append(it)
                }
            }
        }
    }
}