package ru.loginov.serbian.bot.telegram.service.command.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.telegram.service.command.BotCommand
import ru.loginov.serbian.bot.telegram.service.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.util.StringBuilderMarkdownV2
import ru.loginov.serbian.bot.telegram.util.markdown2
import ru.loginov.serbian.bot.telegram.util.markdown2FromString

@Component
class CreateCategoryForceCommand : BotCommand {

    @Autowired
    private lateinit var categoryManager: CategoryManager

    override val commandName: String = "createCategoryForce"
    override val description: StringBuilderMarkdownV2 =
            markdown2FromString("Creates new category and ignore already exists")
    override val usage: StringBuilderMarkdownV2 = markdown2 {
        append("""
            /$commandName <language0>=<category_name0> <language1>=<category_name1>
            /$commandName <command_name> (""".trimIndent()
        )

        italic {
            append("In this case category name will be should in your telegram language")
        }

        append(")")
    }

    override suspend fun execute(context: BotCommandExecuteContext) {
        if (context.arguments.isEmpty()) {
            context.telegramService.sendMessage {
                chatId = context.chatId
                buildText {
                    append("You didn't write category name. Please use command like:\n")
                    append(usage)
                }
            }
        } else {
            val categoryNames = context.arguments.mapNotNull {
                it.split('=').let {
                    if (it.size == 1) {
                        if (it[0].isBlank()) {
                            null
                        } else {
                            (context.lang ?: "en") to it[0]
                        }
                    } else {
                        it[0] to it[1]
                    }
                }
            }.toMap()
            val category = categoryManager.createNewCategory(categoryNames)

            context.telegramService.sendMessage {
                chatId = context.chatId
                buildText {
                    append("Created category with names:\n")
                    category.localization.filter { (_, value) ->
                        value.name != null
                    }.forEach { (lang, value) ->
                        append('\n')
                        append(lang).append(" = ").append(value.name!!)
                    }
                }
            }
        }
    }
}