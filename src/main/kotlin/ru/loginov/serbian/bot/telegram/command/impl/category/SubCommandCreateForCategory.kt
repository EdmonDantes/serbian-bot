package ru.loginov.serbian.bot.telegram.command.impl.category

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand

@Component
@SubCommand(parents = [CategoryBotCommand::class])
@RequiredPermission("commands.category.create")
class SubCommandCreateForCategory : AbstractSubCommand() {

    @Autowired
    private lateinit var categoryManager: CategoryManager

    override val commandName: String = "create"
    override val shortDescription: String? = "Create new category"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val categoryName = context.getNextArgument("Category name")

        if (categoryName.isNullOrEmpty()) {
            context.sendMessage {
                markdown2 {
                    append("Can not create new category with name '$categoryName'")
                }
            }
            return
        }

        val lang = if (context.user.language == null) {
            context.getNextArgument(
                    mapOf("Russian" to "ru", "English" to "en"),
                    "language of category name"
            )
        } else context.user.language!!

        if (lang.isNullOrEmpty() || lang.length != 2) {
            context.sendMessage {
                markdown2 {
                    append("Can not create new category, because we can not known which language used for naming")
                }
            }
            return
        }
        context.sendMessage {
            markdown2 {

                try {
                    categoryManager.createNewCategory(mapOf(lang to categoryName))
                    append("Successful created new category with name '$categoryName'")
                } catch (e: Exception) {
                    LOGGER.warn("Can not create new category with name '$categoryName'", e)
                    append("Successful creating new category with name '$categoryName'")
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandCreateForCategory::class.java)
    }
}