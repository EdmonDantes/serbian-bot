package ru.loginov.serbian.bot.telegram.command.impl.category

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.base.LocalizedSubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.util.markdown2
import ru.loginov.simple.permissions.annotation.RequiredPermission

@Component
@SubCommand(parents = [CategoryBotCommand::class])
@RequiredPermission("commands.category.children")
class SubCommandChildrenForCategory(
        private val categoryManager: CategoryManager
) : LocalizedSubCommand("bot.command.category.children") {

    override val commandName: String = "children"

    override suspend fun BotCommandExecuteContext.action() {
        val categoryId = arguments.argument("categoryId")
                .required()
                .transform { it.toIntOrNull() }
                .validate { it != null }
                .get()!!

        val category = categoryManager.findById(categoryId)
        sendMessage {
            markdown2(localization) {
                if (category == null) {
                    appendKey("_error.can.not.find.category")
                } else {
                    val parentName = categoryManager.findLocalizedNameFor(category, user.language)
                            ?: "ID=${category.id}"
                    if (category.children.isEmpty()) {
                        appendKey("_error.can.not.find.children", parentName)
                    } else {
                        appendKey("_success", parentName)
                        category.children.forEach {
                            val categoryName = categoryManager.findLocalizedNameFor(it, user.language)
                            if (categoryName != null) {
                                append("\n$categoryName -> ${it.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}