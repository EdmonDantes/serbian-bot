package ru.loginov.serbian.bot.telegram.command.impl.category

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand(parents = [CategoryBotCommand::class])
class SubCommandChildrenForCategory : AbstractSubCommand() {

    @Autowired
    private lateinit var categoryManager: CategoryManager

    override val commandName: String = "children"
    override val shortDescription: String? = "@{bot.command.category.children._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val categoryIdStr = context.getNextArgument("@{bot.command.category.children._argument.categoryId}")
        val category = categoryIdStr?.toIntOrNull()?.let { categoryManager.findById(it) }

        context.sendMessage {
            markdown2(context) {
                if (category == null) {
                    append("@{bot.command.category.children._error.can.not.find.category}{$categoryIdStr}")
                } else {
                    val parentName = categoryManager.findLocalizedNameFor(category, context.user.language)
                            ?: "ID=${category.id}"
                    if (category.subCategories.isEmpty()) {
                        append("@{bot.command.category.children._error.can.not.find.children}{$parentName}")
                    } else {
                        append("@{bot.command.category.children._success}{$parentName}")
                        category.subCategories.forEach {
                            val categoryName = categoryManager.findLocalizedNameFor(it, context.user.language)
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