package ru.loginov.serbian.bot.telegram.command.impl.category

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
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

        val parentCategoryIdStr = context.getNextArgument("@{bot.command.category.create._argument.parentId}", true)
        val parentCategoryId = parentCategoryIdStr?.toIntOrNull()
        if (parentCategoryIdStr != null
                && (parentCategoryId == null || categoryManager.findById(parentCategoryId) == null)
        ) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.category.create._error.parent.id.not.found}{$parentCategoryIdStr}")
                }
            }
            return
        }

        val lang = context.user.getInputLanguageOr(suspend {
            context.getNextLanguageArgument("@{bot.command.category.create._argument.language}")
        })

        context.sendMessage {
            markdown2(context) {
                if (lang.isNullOrEmpty() || lang.length != 2) {
                    append("@{bot.command.category.create._.can.not.create.category.unknown.language}")
                } else {
                    val category = try {
                        categoryManager.create(mapOf(lang to categoryName), parentCategoryId)
                    } catch (e: Exception) {
                        LOGGER.warn("Can not create new category with name '$categoryName'", e)
                        null
                    }

                    if (category != null) {
                        append("@{bot.command.category.create._.success}{$categoryName}{${category.id}}")
                    } else {
                        append("@{bot.command.category.create._.can.not.create.category}{$categoryName}")
                    }
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandCreateForCategory::class.java)
    }
}