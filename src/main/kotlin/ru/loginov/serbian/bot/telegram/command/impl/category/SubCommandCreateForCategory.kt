package ru.loginov.serbian.bot.telegram.command.impl.category

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand(parents = [CategoryBotCommand::class])
@RequiredPermission("commands.category.create")
class SubCommandCreateForCategory : AbstractSubCommand() {

    @Autowired
    private lateinit var categoryManager: CategoryManager

    @Autowired
    private lateinit var localizationManager: LocalizationManager

    override val commandName: String = "create"
    override val shortDescription: String? = "@{bot.command.category.create._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val categoryName = context.getNextArgument("@{bot.command.category.create._argument.categoryName}")

        if (categoryName.isNullOrEmpty()) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.category.create._.can.not.create.category}{$categoryName}")
                }
            }
            return
        }

        val languageMenu = localizationManager.allSupportLanguages.associateBy { "@{language.$it}" }

        val lang = if (context.user.language == null) {
            context.getNextArgument(languageMenu, "@{bot.command.category.create._argument.language}")
        } else context.user.language!!

        if (lang.isNullOrEmpty() || lang.length != 2) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.category.create._.can.not.create.category.unknown.language}")
                }
            }
            return
        }
        context.sendMessage {
            markdown2(context) {
                try {
                    categoryManager.createNewCategory(mapOf(lang to categoryName))
                    append("@{bot.command.category.create._.success}{$categoryName}")
                } catch (e: Exception) {
                    LOGGER.warn("Can not create new category with name '$categoryName'", e)
                    append("@{bot.command.category.create._.can.not.create.category}{$categoryName}")
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandCreateForCategory::class.java)
    }
}