package ru.loginov.serbian.bot.telegram.command.impl.category

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.LocalizedSubCommand
import ru.loginov.serbian.bot.util.markdown2
import ru.loginov.simple.localization.impl.localizationKey

@Component
@SubCommand(parents = [CategoryBotCommand::class])
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
                    append(localizationKey("_error.can.not.find.category"))
                } else {
                    val parentName = categoryManager.findLocalizedNameFor(category, user.language)
                            ?: "ID=${category.id}"
                    if (category.subCategories.isEmpty()) {
                        append(localizationKey("_error.can.not.find.children", parentName))
                    } else {
                        append(localizationKey("_success", parentName))
                        category.subCategories.forEach {
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